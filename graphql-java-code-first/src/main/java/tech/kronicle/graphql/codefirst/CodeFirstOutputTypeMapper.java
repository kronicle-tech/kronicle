package tech.kronicle.graphql.codefirst;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.List;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;

@Slf4j
public class CodeFirstOutputTypeMapper extends CodeFirstTypeMapper<GraphQLOutputType, GraphQLFieldDefinition> {

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected GraphQLOutputType newObject(String name, List<GraphQLFieldDefinition> fields) {
        return GraphQLObjectType.newObject()
                .name(name)
                .fields(fields)
                .build();
    }

    @Override
    protected GraphQLFieldDefinition newField(String name, GraphQLOutputType type) {
        return newFieldDefinition()
                .name(name)
                .type(type)
                .build();
    }
}
