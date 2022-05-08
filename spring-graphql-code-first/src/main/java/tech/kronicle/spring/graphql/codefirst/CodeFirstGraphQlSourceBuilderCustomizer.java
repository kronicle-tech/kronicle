package tech.kronicle.spring.graphql.codefirst;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.graphql.execution.GraphQlSource;

@RequiredArgsConstructor
public class CodeFirstGraphQlSourceBuilderCustomizer implements GraphQlSourceBuilderCustomizer {

    private final CodeFirstGraphQlSchemaGenerator graphQlSchemaGenerator;

    @Override
    public void customize(GraphQlSource.SchemaResourceBuilder builder) {
        builder.schemaResources(graphQlSchemaGenerator.generateResource());
    }
}
