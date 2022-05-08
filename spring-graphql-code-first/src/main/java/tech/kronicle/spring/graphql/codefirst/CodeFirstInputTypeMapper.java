package tech.kronicle.spring.graphql.codefirst;

import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputType;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.List;

import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;

@Slf4j
public class CodeFirstInputTypeMapper extends CodeFirstTypeMapper<GraphQLInputType, GraphQLInputObjectField> {

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected GraphQLInputType newObject(String name, List<GraphQLInputObjectField> fields) {
        return newInputObject()
                .name(name)
                .fields(fields)
                .build();
    }

    @Override
    protected GraphQLInputObjectField newField(String name, GraphQLInputType type) {
        return newInputObjectField()
                .name(name)
                .type(type)
                .build();
    }
}
