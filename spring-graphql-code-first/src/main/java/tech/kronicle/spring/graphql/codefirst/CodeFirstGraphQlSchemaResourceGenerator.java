package tech.kronicle.spring.graphql.codefirst;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import tech.kronicle.graphql.codefirst.CodeFirstGraphQlSchemaGenerator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class CodeFirstGraphQlSchemaResourceGenerator {

    private final ListableBeanFactory beanFactory;
    private final CodeFirstGraphQlSchemaGenerator schemaGenerator;

    public ByteArrayResource generateSchemaResource() {
        List<Class> controllerTypes = getControllerTypes();
        List<Method> queryMethods = getSchemaMethods(controllerTypes, QueryMapping.class);
        List<Method> mutationMethods = getSchemaMethods(controllerTypes, MutationMapping.class);
        return createResource(schemaGenerator.addQueryMethods(queryMethods)
                .addMutationMethods(mutationMethods)
                .generateText());
    }

    private List<Class> getControllerTypes() {
        return beanFactory.getBeansWithAnnotation(Controller.class)
                .values()
                .stream()
                .map(Object::getClass)
                .collect(toUnmodifiableList());
    }

    private <A extends Annotation> List<Method> getSchemaMethods(List<Class> controllerTypes, Class<A> annotationType) {
        return controllerTypes.stream()
                .map(controllerType -> getSchemaMethods(controllerType, annotationType))
                .flatMap(Collection::stream)
                .collect(toUnmodifiableList());
    }

    private <A extends Annotation> List<Method> getSchemaMethods(Class controllerType, Class<A> annotationType) {
        return Arrays.stream(controllerType.getDeclaredMethods())
                .filter(method -> nonNull(method.getAnnotation(annotationType)))
                .collect(toUnmodifiableList());
    }

    private ByteArrayResource createResource(String schemaText) {
        return new ByteArrayResource(schemaText.getBytes(StandardCharsets.UTF_8));
    }
}
