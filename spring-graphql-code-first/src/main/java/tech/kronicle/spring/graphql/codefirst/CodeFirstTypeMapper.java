package tech.kronicle.spring.graphql.codefirst;

import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLType;
import org.slf4j.Logger;
import tech.kronicle.graphql.codefirst.annotation.CodeFirstGraphQlIgnore;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLFloat;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLList.list;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableSet;

public abstract class CodeFirstTypeMapper<T extends GraphQLType, F> {

    private static final Set<Type> INTEGER_TYPES = Set.of(Byte.class, Short.class, Integer.class, Long.class);
    private static final Set<Type> FLOAT_TYPES = Set.of(Float.class, Double.class);

    private final Map<Type, T> graphQlTypeCache = new HashMap<>();

    public T resolveGraphQlType(Type type) {
        return resolveGraphQlType(type, Set.of());
    }

    private T resolveGraphQlType(Type type, Set<Type> visitedTypes) {
        log().debug("Resolving type " + type.getTypeName());
        if (visitedTypes.contains(type)) {
            throw new IllegalStateException("Cyclic dependency found for type " + type.getTypeName());
        }
        if (INTEGER_TYPES.contains(type)) {
            return (T) GraphQLInt;
        } else if (FLOAT_TYPES.contains(type)) {
            return (T) GraphQLFloat;
        } else if (type.equals(String.class)) {
            return (T) GraphQLString;
        } else if (type.equals(Boolean.class)) {
            return (T) GraphQLBoolean;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();

            if (rawType.equals(List.class)) {
                Type elementType = parameterizedType.getActualTypeArguments()[0];
                return (T) list(resolveGraphQlType(elementType));
            } else {
                throw new IllegalStateException("Unexpected parameterized type " + rawType.getTypeName());
            }
        } else if (type instanceof Class) {
            Class clazz = (Class) type;

            if (clazz.isEnum()) {
                return (T) GraphQLString;
            } else {
                T graphQlType = graphQlTypeCache.get(type);

                if (isNull(graphQlType)) {
                    graphQlType = newObject(
                            clazz.getSimpleName(),
                            getFields(clazz, newVisitedTypes(visitedTypes, type))
                    );
                    graphQlTypeCache.put(type, graphQlType);
                }
                return graphQlType;
            }
        } else {
            throw new IllegalStateException("Unexpected type " + type.getTypeName());
        }
    }

    private Set<Type> newVisitedTypes(Set<Type> visitedTypes, Type newType) {
        return Stream.of(visitedTypes, Set.of(newType))
                .flatMap(Collection::stream)
                .collect(toUnmodifiableSet());
    }

    private List<F> getFields(Class clazz, Set<Type> visitedTypes) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(this::fieldIsNotStatic)
                .filter(this::fieldIsNotIgnored)
                .map(field -> getField(field, visitedTypes))
                .collect(toUnmodifiableList());
    }

    private boolean fieldIsNotStatic(Field field) {
        return !Modifier.isStatic(field.getModifiers());
    }

    private boolean fieldIsNotIgnored(Field field) {
        return !field.isAnnotationPresent(CodeFirstGraphQlIgnore.class);
    }

    private F getField(Field field, Set<Type> visitedTypes) {
        T fieldType = resolveGraphQlType(field.getGenericType(), visitedTypes);
        if (nonNullField(field)) {
            fieldType = (T) GraphQLNonNull.nonNull(fieldType);
        }
        return newField(field.getName(), fieldType);
    }

    private boolean nonNullField(Field field) {
        return field.isAnnotationPresent(NotNull.class) ||
                field.isAnnotationPresent(NotEmpty.class);
    }

    protected abstract Logger log();

    protected abstract T newObject(String name, List<F> fields);

    protected abstract F newField(String name, T type);
}
