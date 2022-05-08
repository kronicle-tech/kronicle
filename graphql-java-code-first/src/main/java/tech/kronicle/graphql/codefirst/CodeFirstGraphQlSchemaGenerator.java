package tech.kronicle.graphql.codefirst;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaPrinter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class CodeFirstGraphQlSchemaGenerator {

    private final CodeFirstInputTypeMapper inputTypeMapper;
    private final CodeFirstOutputTypeMapper outputTypeMapper;
    private final Set<Method> queryMethods = new HashSet<>();
    private final Set<Method> mutationMethods = new HashSet<>();

    public CodeFirstGraphQlSchemaGenerator addQueryMethods(List<Method> queryMethods) {
        this.queryMethods.addAll(queryMethods);
        return this;
    }

    public CodeFirstGraphQlSchemaGenerator addMutationMethods(List<Method> mutationMethods) {
        this.mutationMethods.addAll(mutationMethods);
        return this;
    }

    public GraphQLSchema generate() {
        GraphQLSchema.Builder builder = GraphQLSchema.newSchema();
        GraphQLObjectType query = buildSchema("Query", queryMethods);
        if (schemaIsNotEmpty(query)) {
            builder.query(query);
        }
        GraphQLObjectType mutation = buildSchema("Mutation", mutationMethods);
        if (schemaIsNotEmpty(mutation)) {
            builder.mutation(mutation);
        }
        builder.additionalTypes(inputTypeMapper.resolveCustomTypes());
        builder.additionalTypes(outputTypeMapper.resolveCustomTypes());
        return builder
                .build();
    }

    public String generateText() {
        return new SchemaPrinter().print(generate());
    }

    private boolean schemaIsNotEmpty(GraphQLObjectType schema) {
        return !schema.getFields().isEmpty();
    }

    private GraphQLObjectType buildSchema(String name, Set<Method> schemaMethods) {
        return GraphQLObjectType.newObject()
                .name(name)
                .fields(buildOperationFields(schemaMethods))
                .build();
    }

    private List<GraphQLFieldDefinition> buildOperationFields(Set<Method> schemaMethods) {
        return schemaMethods.stream()
                .map(this::buildOperationField)
                .collect(toUnmodifiableList());
    }

    private GraphQLFieldDefinition buildOperationField(Method schemaMethod) {
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(schemaMethod.getName())
                .arguments(buildOperationArguments(schemaMethod))
                .type(outputTypeMapper.resolveType(schemaMethod.getReturnType()))
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
                .type(inputTypeMapper.resolveType(parameter.getType()))
                .build();
    }
}
