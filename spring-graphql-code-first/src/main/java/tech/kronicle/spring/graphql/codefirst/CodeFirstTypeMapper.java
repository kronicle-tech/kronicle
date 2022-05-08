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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLFloat;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLList.list;
import static graphql.schema.GraphQLTypeReference.typeRef;
import static java.util.stream.Collectors.toUnmodifiableList;

public abstract class CodeFirstTypeMapper<T extends GraphQLType, F> {

    private static final Set<Type> INTEGER_TYPES = Set.of(
            byte.class,
            Byte.class,
            short.class,
            Short.class,
            int.class,
            Integer.class,
            long.class,
            Long.class
    );
    private static final Set<Type> FLOAT_TYPES = Set.of(
            float.class,
            Float.class,
            double.class,
            Double.class
    );
    private static final Set<Type> STRING_TYPES = Set.of(
            String.class,
            LocalDate.class,
            LocalDateTime.class,
            ZonedDateTime.class,
            OffsetDateTime.class
    );

    private final List<Class> customTypes = new ArrayList<>();

    public T resolveType(Type type) {
        log().debug("Resolving type " + type.getTypeName());
        if (INTEGER_TYPES.contains(type)) {
            return (T) GraphQLInt;
        } else if (FLOAT_TYPES.contains(type)) {
            return (T) GraphQLFloat;
        } else if (STRING_TYPES.contains(type)) {
            return (T) GraphQLString;
        } else if (type.equals(Boolean.class)) {
            return (T) GraphQLBoolean;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();

            if (rawType.equals(List.class)) {
                Type elementType = parameterizedType.getActualTypeArguments()[0];
                return (T) list(resolveType(elementType));
            } else {
                throw new IllegalStateException("Unexpected parameterized type " + rawType.getTypeName());
            }
        } else if (type instanceof Class) {
            Class clazz = (Class) type;

            if (clazz.isEnum()) {
                return (T) GraphQLString;
            } else {
                if (!customTypes.contains(clazz)) {
                    customTypes.add(clazz);
                }
                return (T) typeRef(clazz.getSimpleName());
            }
        } else {
            throw new IllegalStateException("Unexpected type " + type.getTypeName());
        }
    }

    public Set<GraphQLType> resolveCustomTypes() {
        Set<GraphQLType> resolvedCustomTypes = new HashSet<>();
        // Note: customTypes will grow with some iterations of this for look as new nested custom types are found
        for (int index = 0; index < customTypes.size(); index++) {
            Class customType = customTypes.get(index);
            resolvedCustomTypes.add(resolveCustomType(customType));
        }
        return Set.copyOf(resolvedCustomTypes);
    }

    private T resolveCustomType(Class type) {
        return newObject(
                type.getSimpleName(),
                getFields(type)
        );
    }

    private List<F> getFields(Class clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(this::fieldIsNotStatic)
                .filter(this::fieldIsNotIgnored)
                .map(field -> getField(field))
                .collect(toUnmodifiableList());
    }

    private boolean fieldIsNotStatic(Field field) {
        return !Modifier.isStatic(field.getModifiers());
    }

    private boolean fieldIsNotIgnored(Field field) {
        return !field.isAnnotationPresent(CodeFirstGraphQlIgnore.class);
    }

    private F getField(Field field) {
        T fieldType = resolveType(field.getGenericType());
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
