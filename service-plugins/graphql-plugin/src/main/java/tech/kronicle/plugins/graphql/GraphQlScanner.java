package tech.kronicle.plugins.graphql;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.scanners.ComponentAndCodebaseScanner;
import tech.kronicle.pluginapi.scanners.models.ComponentAndCodebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.graphql.models.SchemaAndErrors;
import tech.kronicle.plugins.graphql.services.SchemaFetcher;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.GraphQlSchemasState;
import tech.kronicle.sdk.models.ScannerError;
import tech.kronicle.sdk.models.graphql.GraphQlSchema;

import javax.inject.Inject;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;

import static java.util.stream.Collectors.toUnmodifiableList;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class GraphQlScanner extends ComponentAndCodebaseScanner {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    private final SchemaFetcher fetcher;

    @Override
    public String id() {
        return "graphql";
    }

    @Override
    public String description() {
        return "Loads GraphQL schemas for components so they can be rendered in Kronicle";
    }

    @Override
    public Output<Void, Component> scan(ComponentAndCodebase input) {
        List<SchemaAndErrors> schemaAndErrors = fetcher.fetchSchemas(id(), input.getCodebase().getDir(), input.getComponent().getGraphQlSchemas());
        List<GraphQlSchema> schemas = getSchemas(schemaAndErrors);
        UnaryOperator<Component> transformer;

        if (schemas.isEmpty()) {
            transformer = null;
        } else {
            transformer = (Component component) -> component.addState(getSchemasState(schemas));
        }

        return Output.builder(CACHE_TTL)
                .transformer(transformer)
                .errors(getErrors(schemaAndErrors))
                .build();
    }

    private GraphQlSchemasState getSchemasState(List<GraphQlSchema> schemas) {
        return new GraphQlSchemasState(
                GraphQlPlugin.ID,
                schemas
        );
    }

    private List<GraphQlSchema> getSchemas(List<SchemaAndErrors> schemaAndErrors) {
        return schemaAndErrors.stream()
                .map(SchemaAndErrors::getSchema)
                .collect(toUnmodifiableList());
    }

    private List<ScannerError> getErrors(List<SchemaAndErrors> schemaAndErrors) {
        return schemaAndErrors.stream()
                .map(SchemaAndErrors::getErrors)
                .flatMap(Collection::stream)
                .collect(toUnmodifiableList());
    }
}
