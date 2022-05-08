package tech.kronicle.spring.graphql.codefirst;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaPrinter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class CodeFirstGraphQlSchemaGenerator {

    private final ListableBeanFactory beanFactory;
    private final CodeFirstInputTypeMapper inputTypeMapper;
    private final CodeFirstOutputTypeMapper outputTypeMapper;
    private List<Class> controllerTypes;

    @PostConstruct
    public void postConstruct() {
        controllerTypes = getControllerTypes();
    }

    public GraphQLSchema generate() {
        GraphQLSchema.Builder builder = GraphQLSchema.newSchema();
        GraphQLObjectType query = buildSchema("query", controllerTypes, QueryMapping.class);
        if (schemaIsNotEmpty(query)) {
            builder.query(query);
        }
        GraphQLObjectType mutation = buildSchema("mutation", controllerTypes, MutationMapping.class);
        if (schemaIsNotEmpty(mutation)) {
            builder.mutation(mutation);
        }
        return builder
                .build();
    }

    private boolean schemaIsNotEmpty(GraphQLObjectType schema) {
        return !schema.getFields().isEmpty();
    }

    private List<Class> getControllerTypes() {
        return beanFactory.getBeansWithAnnotation(Controller.class)
                .values()
                .stream()
                .map(Object::getClass)
                .collect(toUnmodifiableList());
    }

    public String generateText() {
        return new SchemaPrinter().print(generate());
    }

    public ByteArrayResource generateResource() {
        return new ByteArrayResource(generateText().getBytes(StandardCharsets.UTF_8));
    }

    private <A extends Annotation> GraphQLObjectType buildSchema(
            String name,
            List<Class> controllerTypes,
            Class<A> annotationType
    ) {
        return buildSchema(name, getSchemaMethods(controllerTypes, annotationType));
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

    private GraphQLObjectType buildSchema(String name, List<Method> method) {
        return GraphQLObjectType.newObject()
                .name(name)
                .fields(buildOperationFields(method))
                .build();
    }

    private List<GraphQLFieldDefinition> buildOperationFields(List<Method> method) {
        return method.stream()
                .map(this::buildOperationField)
                .collect(toUnmodifiableList());
    }

    private GraphQLFieldDefinition buildOperationField(Method method) {
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(method.getName())
                .arguments(buildOperationArguments(method))
                .type(outputTypeMapper.resolveGraphQlType(method.getReturnType()))
                .build();
    }

    private List<GraphQLArgument> buildOperationArguments(Method method) {
        return Arrays.stream(method.getParameters())
                .map(this::buildOperationArgument)
                .collect(toUnmodifiableList());
    }

    private GraphQLArgument buildOperationArgument(Parameter parameter) {
        return GraphQLArgument.newArgument()
                .name(parameter.getName())
                .type(inputTypeMapper.resolveGraphQlType(parameter.getType()))
                .build();
    }
}
