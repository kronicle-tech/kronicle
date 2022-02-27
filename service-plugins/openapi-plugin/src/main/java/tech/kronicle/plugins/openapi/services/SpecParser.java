package tech.kronicle.plugins.openapi.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import tech.kronicle.pluginapi.scanners.Scanner;
import tech.kronicle.pluginapi.scanners.models.ComponentAndCodebase;
import tech.kronicle.plugins.openapi.models.SpecAndErrors;
import tech.kronicle.sdk.models.openapi.OpenApiSpec;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SpecParser {

    private final Map<String, SwaggerParseResult> swaggerParseResultCache = new HashMap<>();
    private final OpenAPIV3Parser openApiV3Parser = new OpenAPIV3Parser();

    private final ObjectMapper objectMapper;
    private final SpecErrorProcessor specErrorProcessor;

    public void clearCache() {
        swaggerParseResultCache.clear();
    }

    public List<SpecAndErrors> parseSpecs(Scanner<?, ?> scanner, ComponentAndCodebase input, List<OpenApiSpec> specs) {
        return specs.stream()
                .map(parsePossibleSpec(scanner, input))
                .collect(Collectors.toList());
    }

    private Function<OpenApiSpec, SpecAndErrors> parsePossibleSpec(Scanner<?, ?> scanner, ComponentAndCodebase input) {
        return spec -> {
            String location = nonNull(spec.getUrl()) ? spec.getUrl() : resolveFileRelativeToCodebase(input, spec);
            SwaggerParseResult swaggerParseResult = swaggerParseResultCache.get(location);

            if (isNull(swaggerParseResult)) {
                ParseOptions options = new ParseOptions();
                options.setResolve(true);
                options.setResolveCombinators(true);
                options.setResolveFully(true);
                try {
                    swaggerParseResult = openApiV3Parser.readLocation(location, null, options);
                } catch (Exception e) {
                    swaggerParseResultCache.put(location, new SwaggerParseResult());
                    return new SpecAndErrors(spec, List.of(specErrorProcessor.getError(scanner, location, e)));
                }
                swaggerParseResultCache.put(location, swaggerParseResult);
            }

            return new SpecAndErrors(spec.withSpec(convertSwaggerSpecToRawJson(swaggerParseResult)), specErrorProcessor.getErrors(scanner, location, swaggerParseResult));
        };
    }

    private String resolveFileRelativeToCodebase(ComponentAndCodebase input, OpenApiSpec spec) {
        return input.getCodebase().getDir().resolve(spec.getFile()).toString();
    }

    @SneakyThrows
    private String convertSwaggerSpecToRawJson(SwaggerParseResult swaggerParseResult) {
        OpenAPI openApi = swaggerParseResult.getOpenAPI();

        if (isNull(openApi)) {
            return null;
        }

        return objectMapper.writeValueAsString(openApi);
    }
}
