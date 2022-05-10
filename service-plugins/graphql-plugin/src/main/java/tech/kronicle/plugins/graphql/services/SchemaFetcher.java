package tech.kronicle.plugins.graphql.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.graphql.models.SchemaAndErrors;
import tech.kronicle.sdk.models.ScannerError;
import tech.kronicle.sdk.models.graphql.GraphQlSchema;
import tech.kronicle.utils.ThrowableToScannerErrorMapper;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SchemaFetcher {

    private final SchemaFileReader fileReader;
    private final SchemaDownloader downloader;
    private final SchemaTransformer transformer;
    private final ThrowableToScannerErrorMapper scannerErrorMapper;

    public List<SchemaAndErrors> fetchSchemas(String scannerId, Path codebaseDir, List<GraphQlSchema> schemas) {
        return schemas.stream()
                .map(schema -> fetchSchema(scannerId, codebaseDir, schema))
                .collect(toUnmodifiableList());
    }

    private SchemaAndErrors fetchSchema(String scannerId, Path codebaseDir, GraphQlSchema schema) {
        if (nonNull(schema.getFile()) && nonNull(schema.getUrl())) {
            return error(scannerId, schema, "Schema cannot have both file and url");
        } else if (isNull(schema.getFile()) && isNull(schema.getUrl())) {
            return error(scannerId, schema, "Schema does not have file or url set");
        }

        String schemaText;

        try {
            if (nonNull(schema.getFile())) {
                schemaText = fileReader.readSchemaFile(codebaseDir, schema.getFile());
            } else {
                String introspectionResult = downloader.downloadSchema(schema.getUrl());
                schemaText = transformer.transformIntrospectionResultToSchemaIdl(introspectionResult);
            }
        } catch (Exception e) {
            return error(schema, scannerErrorMapper.map(scannerId, e));
        }

        return new SchemaAndErrors(schema.withSchema(schemaText), List.of());
    }

    private SchemaAndErrors error(String scannerId, GraphQlSchema schema, String message) {
        return error(schema, new ScannerError(scannerId, message, null));
    }

    private SchemaAndErrors error(GraphQlSchema schema, ScannerError error) {
        return new SchemaAndErrors(schema, List.of(error));
    }
}
