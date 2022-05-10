package tech.kronicle.plugins.graphql.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import graphql.introspection.IntrospectionResultToSchema;
import graphql.language.Document;
import graphql.schema.idl.SchemaPrinter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.inject.Inject;
import java.util.Map;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SchemaTransformer {

    private final ObjectMapper objectMapper;
    private final IntrospectionResultToSchema introspectionResultToSchema;
    private final SchemaPrinter schemaPrinter;

    @SneakyThrows
    public String transformIntrospectionResultToSchemaIdl(String introspectionResult) {
        Map<String, Object> introspectionResultMap = JsonToJavaTypeConverter.toMap(
                (ObjectNode) objectMapper.readTree(introspectionResult)
        );
        Document schemaIdl = introspectionResultToSchema.createSchemaDefinition(
                (Map<String, Object>) introspectionResultMap.get("data")
        );
        return schemaPrinter.print(schemaIdl);
    }
}
