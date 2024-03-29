package tech.kronicle.plugins.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.ComponentAndCodebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.openapi.config.OpenApiConfig;
import tech.kronicle.plugins.openapi.services.SpecDiscoverer;
import tech.kronicle.plugins.openapi.services.SpecErrorProcessor;
import tech.kronicle.plugins.openapi.services.SpecParser;
import tech.kronicle.plugintestutils.scanners.BaseCodebaseScannerTest;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.openapi.OpenApiSpecsState;
import tech.kronicle.sdk.models.ScannerError;
import tech.kronicle.sdk.models.openapi.OpenApiSpec;
import tech.kronicle.utils.ThrowableToScannerErrorMapper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.utils.FileUtilsFactory.createFileUtils;
import static tech.kronicle.utils.JsonMapperFactory.createJsonMapper;

public class OpenApiScannerTest extends BaseCodebaseScannerTest {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    private WireMockServer wireMockServer;
    private final ObjectMapper objectMapper = createJsonMapper();
    private MappingBuilder openApiSpecWireMockStub;

    @AfterEach
    public void afterEach() {
        if (nonNull(wireMockServer)) {
            wireMockServer.stop();
        }
    }

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // Given
        OpenApiScanner underTest = createOpenApiScanner(true);

        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("openapi");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // Given
        OpenApiScanner underTest = createOpenApiScanner(true);

        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("This does two things: a) scan's a component's codebase for any YAML or JSON files that contain OpenAPI specs and b) "
                + "uses any OpenAPI spec URLs specified in a component's metadata");
    }

    @Test
    public void notesShouldReturnNull() {
        // Given
        OpenApiScanner underTest = createOpenApiScanner(true);

        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void refreshShouldClearSwaggerParserCache() {
        // Given
        createWireMockServer();
        String openApiSpecUrl = wireMockServer.baseUrl() + "/openapi-spec";
        OpenApiSpec openApiSpec = OpenApiSpec.builder()
                .url(openApiSpecUrl)
                .build();
        Component component = Component.builder()
                .openApiSpecs(List.of(openApiSpec))
                .build();
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("NoOpenApiSpecs"));
        ComponentAndCodebase componentAndCodebase = new ComponentAndCodebase(component, codebase);
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        OpenApiScanner underTest = createOpenApiScanner(true);
        underTest.refresh(componentMetadata);

        // When
        Output<Void, Component> returnValue = underTest.scan(componentAndCodebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        List<OpenApiSpec> returnOpenApiSpecs = getSpecs(returnValue);
        assertThat(returnOpenApiSpecs).hasSize(1);
        assertThat(getSpecAsJsonTree(returnOpenApiSpecs.get(0)).get("info").get("title").textValue()).isEqualTo("Example");

        // Given
        changeWireMockHostedOpenApiSpec();
        underTest.refresh(componentMetadata);

        // When
        returnValue = underTest.scan(componentAndCodebase);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        returnOpenApiSpecs = getSpecs(returnValue);
        assertThat(returnOpenApiSpecs).hasSize(1);
        assertThat(getSpecAsJsonTree(returnOpenApiSpecs.get(0)).get("info").get("title").textValue()).isEqualTo("Example - Changed");
    }

    @Test
    public void scanShouldHandleNoOpenApiSpecs() {
        // Given
        Component component = Component.builder().build();
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("NoOpenApiSpecs"));
        ComponentAndCodebase componentAndCodebase = new ComponentAndCodebase(component, codebase);
        OpenApiScanner underTest = createOpenApiScanner(true);

        // When
        Output<Void, Component> returnValue = underTest.scan(componentAndCodebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        assertNoState(returnValue);
    }

    @Test
    public void scanShouldHandleManualOpenApiSpec() {
        // Given
        createWireMockServer();
        String openApiSpecUrl = wireMockServer.baseUrl() + "/openapi-spec";
        OpenApiSpec openApiSpec = OpenApiSpec.builder()
                .url(openApiSpecUrl)
                .build();
        Component component = Component.builder()
                .openApiSpecs(List.of(openApiSpec))
                .build();
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("NoOpenApiSpecs"));
        ComponentAndCodebase componentAndCodebase = new ComponentAndCodebase(component, codebase);
        OpenApiScanner underTest = createOpenApiScanner(true);

        // When
        Output<Void, Component> returnValue = underTest.scan(componentAndCodebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        List<OpenApiSpec> returnOpenApiSpecs = getSpecs(returnValue);
        assertThat(returnOpenApiSpecs).hasSize(1);
        OpenApiSpec returnOpenApiSpec;
        returnOpenApiSpec = returnOpenApiSpecs.get(0);
        assertThat(returnOpenApiSpec.getScannerId()).isNull();
        assertThat(returnOpenApiSpec.getFile()).isNull();
        assertThat(returnOpenApiSpec.getUrl()).isEqualTo(openApiSpecUrl);
        assertThat(returnOpenApiSpec.getSpec()).isNotNull();
        assertThat(getSpecAsJsonTree(returnOpenApiSpec).has("openapi")).isTrue();
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    public void scanShouldHandleCodebaseOpenApiSpecs(boolean scanCodebases) {
        // Given
        Component component = Component.builder().build();
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("OpenApiSpecsWithAllFileExtensions"));
        ComponentAndCodebase componentAndCodebase = new ComponentAndCodebase(component, codebase);
        OpenApiScanner underTest = createOpenApiScanner(scanCodebases);

        // When
        Output<Void, Component> returnValue = underTest.scan(componentAndCodebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));

        if (scanCodebases) {
            List<OpenApiSpec> returnOpenApiSpecs = new ArrayList<>(getSpecs(returnValue));
            assertThat(returnOpenApiSpecs).hasSize(3);
            returnOpenApiSpecs.sort(Comparator.comparing(OpenApiSpec::getFile));
            OpenApiSpec returnOpenApiSpec;
            returnOpenApiSpec = returnOpenApiSpecs.get(0);
            assertThat(returnOpenApiSpec.getScannerId()).isEqualTo("openapi");
            assertThat(returnOpenApiSpec.getFile()).isEqualTo("test-openapi.json");
            assertThat(returnOpenApiSpec.getUrl()).isNull();
            assertThat(returnOpenApiSpec.getSpec()).isNotNull();
            assertThat(getSpecAsJsonTree(returnOpenApiSpec).has("openapi")).isTrue();
            returnOpenApiSpec = returnOpenApiSpecs.get(1);
            assertThat(returnOpenApiSpec.getScannerId()).isEqualTo("openapi");
            assertThat(returnOpenApiSpec.getFile()).isEqualTo("test-openapi.yaml");
            assertThat(returnOpenApiSpec.getUrl()).isNull();
            assertThat(returnOpenApiSpec.getSpec()).isNotNull();
            assertThat(getSpecAsJsonTree(returnOpenApiSpec).has("openapi")).isTrue();
            returnOpenApiSpec = returnOpenApiSpecs.get(2);
            assertThat(returnOpenApiSpec.getScannerId()).isEqualTo("openapi");
            assertThat(returnOpenApiSpec.getFile()).isEqualTo("test-openapi.yml");
            assertThat(returnOpenApiSpec.getUrl()).isNull();
            assertThat(returnOpenApiSpec.getSpec()).isNotNull();
            assertThat(getSpecAsJsonTree(returnOpenApiSpec).has("openapi")).isTrue();
        } else {
            assertNoState(returnValue);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    public void scanShouldHandleAManualCodebaseOpenApiSpec(boolean scanCodebases) {
        // Given
        OpenApiSpec openApiSpec = OpenApiSpec.builder()
                .file("test-openapi.yaml")
                .build();
        Component component = Component.builder()
                .openApiSpecs(List.of(openApiSpec))
                .build();
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("OneOpenApiSpec"));
        ComponentAndCodebase componentAndCodebase = new ComponentAndCodebase(component, codebase);
        OpenApiScanner underTest = createOpenApiScanner(scanCodebases);

        // When
        Output<Void, Component> returnValue = underTest.scan(componentAndCodebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        List<OpenApiSpec> returnOpenApiSpecs = getSpecs(returnValue);
        assertThat(returnOpenApiSpecs).hasSize(1);
        OpenApiSpec returnOpenApiSpec;
        returnOpenApiSpec = returnOpenApiSpecs.get(0);
        assertThat(returnOpenApiSpec.getScannerId()).isNull();
        assertThat(returnOpenApiSpec.getFile()).isEqualTo("test-openapi.yaml");
        assertThat(returnOpenApiSpec.getUrl()).isNull();
        assertThat(returnOpenApiSpec.getSpec()).isNotNull();
        assertThat(getSpecAsJsonTree(returnOpenApiSpec).has("openapi")).isTrue();
    }

    @Test
    public void scanShouldHandleAnInvalidOpenApiSpec() {
        // Given
        Component component = Component.builder()
                .build();
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("InvalidOpenApiSpec"));
        ComponentAndCodebase componentAndCodebase = new ComponentAndCodebase(component, codebase);
        OpenApiScanner underTest = createOpenApiScanner(true);

        // When
        Output<Void, Component> returnValue = underTest.scan(componentAndCodebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        List<OpenApiSpec> returnOpenApiSpecs = getSpecs(returnValue);
        assertThat(returnOpenApiSpecs).hasSize(1);
        OpenApiSpec returnOpenApiSpec;
        returnOpenApiSpec = returnOpenApiSpecs.get(0);
        assertThat(returnOpenApiSpec.getScannerId()).isEqualTo("openapi");
        assertThat(returnOpenApiSpec.getFile()).isEqualTo("test-openapi.yaml");
        assertThat(returnOpenApiSpec.getUrl()).isNull();
        assertThat(returnOpenApiSpec.getSpec()).isNotNull();
        assertThat(getSpecAsJsonTree(returnOpenApiSpec).has("openapi")).isTrue();
        assertThat(getSpecAsJsonTree(returnOpenApiSpec).path("paths").path("/example").path("get").path("description").textValue()).isEqualTo("Example");
    }

    @Test
    public void scanShouldHandleAManualInvalidOpenApiSpec() {
        // Given
        OpenApiSpec openApiSpec = OpenApiSpec.builder()
                .file("test-openapi.yaml")
                .build();
        Component component = Component.builder()
                .openApiSpecs(List.of(openApiSpec))
                .build();
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("InvalidOpenApiSpec"));
        ComponentAndCodebase componentAndCodebase = new ComponentAndCodebase(component, codebase);
        OpenApiScanner underTest = createOpenApiScanner(true);

        // When
        Output<Void, Component> returnValue = underTest.scan(componentAndCodebase);

        // Then
        assertThat(returnValue.getErrors()).hasSize(1);
        assertThat(returnValue.getCacheTtl()).isEqualTo(CACHE_TTL);
        ScannerError error;
        error = returnValue.getErrors().get(0);
        assertThat(error.getScannerId()).isEqualTo("openapi");
        assertThat(sanitizeErrorMessage(error.getMessage())).isEqualTo("Issue while parsing OpenAPI spec \"InvalidOpenApiSpec/test-openapi.yaml\": "
                + "attribute paths./invalid is not of type `object`");
        List<OpenApiSpec> returnOpenApiSpecs = getSpecs(returnValue);
        assertThat(returnOpenApiSpecs).hasSize(1);
        OpenApiSpec returnOpenApiSpec;
        returnOpenApiSpec = returnOpenApiSpecs.get(0);
        assertThat(returnOpenApiSpec.getScannerId()).isNull();
        assertThat(returnOpenApiSpec.getFile()).isEqualTo("test-openapi.yaml");
        assertThat(returnOpenApiSpec.getUrl()).isNull();
        assertThat(returnOpenApiSpec.getSpec()).isNotNull();
        assertThat(getSpecAsJsonTree(returnOpenApiSpec).has("openapi")).isTrue();
        assertThat(getSpecAsJsonTree(returnOpenApiSpec).path("paths").path("/example").path("get").path("description").textValue()).isEqualTo("Example");
    }

    @Test
    public void scanShouldHandleAnInvalidYamlFile() {
        // Given
        Component component = Component.builder()
                .build();
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("InvalidYamlFile"));
        ComponentAndCodebase componentAndCodebase = new ComponentAndCodebase(component, codebase);
        OpenApiScanner underTest = createOpenApiScanner(true);

        // When
        Output<Void, Component> returnValue = underTest.scan(componentAndCodebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        List<OpenApiSpec> returnOpenApiSpecs = getMutatedComponentIgnoringErrors(returnValue).getOpenApiSpecs();
        assertThat(returnOpenApiSpecs).isEmpty();
    }

    @Test
    public void scanShouldHandleAManualInvalidYamlFile() {
        // Given
        OpenApiSpec openApiSpec = OpenApiSpec.builder()
                .file("test-openapi.yaml")
                .build();
        Component component = Component.builder()
                .openApiSpecs(List.of(openApiSpec))
                .build();
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("InvalidYamlFile"));
        ComponentAndCodebase componentAndCodebase = new ComponentAndCodebase(component, codebase);
        OpenApiScanner underTest = createOpenApiScanner(true);

        // When
        Output<Void, Component> returnValue = underTest.scan(componentAndCodebase);

        // Then
        assertThat(returnValue.getErrors()).hasSize(1);
        assertThat(returnValue.getCacheTtl()).isEqualTo(CACHE_TTL);
        ScannerError error;
        error = returnValue.getErrors().get(0);
        assertThat(error.getScannerId()).isEqualTo("openapi");
        assertThat(sanitizeErrorMessage(error.getMessage())).isEqualTo("Issue while parsing OpenAPI spec \"InvalidYamlFile/test-openapi.yaml\": attribute openapi is not of type `string`");
        List<OpenApiSpec> returnOpenApiSpecs = getSpecs(returnValue);
        assertThat(returnOpenApiSpecs).hasSize(1);
        OpenApiSpec returnOpenApiSpec;
        returnOpenApiSpec = returnOpenApiSpecs.get(0);
        assertThat(returnOpenApiSpec.getScannerId()).isNull();
        assertThat(returnOpenApiSpec.getFile()).isEqualTo("test-openapi.yaml");
        assertThat(returnOpenApiSpec.getUrl()).isNull();
        assertThat(returnOpenApiSpec.getSpec()).isNull();
    }

    @Test
    public void scanShouldHandleAnOpenApiSpecWithFileReference() {
        // Given
        Component component = Component.builder()
                .build();
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("OpenApiSpecWithFileReference"));
        ComponentAndCodebase componentAndCodebase = new ComponentAndCodebase(component, codebase);
        OpenApiScanner underTest = createOpenApiScanner(true);

        // When
        Output<Void, Component> returnValue = underTest.scan(componentAndCodebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        List<OpenApiSpec> returnOpenApiSpecs = getSpecs(returnValue);
        assertThat(returnOpenApiSpecs).hasSize(1);
        OpenApiSpec returnOpenApiSpec;
        returnOpenApiSpec = returnOpenApiSpecs.get(0);
        assertThat(returnOpenApiSpec.getScannerId()).isEqualTo("openapi");
        assertThat(returnOpenApiSpec.getFile()).isEqualTo("test-openapi.yaml");
        assertThat(returnOpenApiSpec.getUrl()).isNull();
        assertThat(returnOpenApiSpec.getSpec()).isNotNull();
        assertThat(getSpecAsJsonTree(returnOpenApiSpec).has("openapi")).isTrue();
        assertThat(getSpecAsJsonTree(returnOpenApiSpec).path("paths").path("/example").path("get").path("description").textValue()).isEqualTo("Example GET in referenced file");
    }

    @Test
    public void scanShouldIgnoreNonOpenApiYamlAndJsonFiles() {
        // Given
        Component component = Component.builder()
                .build();
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("NonOpenApiYamlAndJsonFiles"));
        ComponentAndCodebase componentAndCodebase = new ComponentAndCodebase(component, codebase);
        OpenApiScanner underTest = createOpenApiScanner(true);

        // When
        Output<Void, Component> returnValue = underTest.scan(componentAndCodebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        assertNoState(returnValue);
    }

    private OpenApiScanner createOpenApiScanner(boolean scanCodebases) {
        SpecDiscoverer specDiscoverer = new SpecDiscoverer(createFileUtils());
        SpecErrorProcessor specErrorProcessor = new SpecErrorProcessor(new ThrowableToScannerErrorMapper());
        SpecParser specParser = new SpecParser(new ObjectMapper(), specErrorProcessor);
        return new OpenApiScanner(specDiscoverer, specParser, new OpenApiConfig(scanCodebases));
    }

    private void createWireMockServer() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        openApiSpecWireMockStub = get(urlPathEqualTo("/openapi-spec"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/x-yaml")
                        .withBody(createOpenApiSpecString("")));
        wireMockServer.stubFor(openApiSpecWireMockStub);
        wireMockServer.start();
    }

    private void changeWireMockHostedOpenApiSpec() {
        wireMockServer.removeStub(openApiSpecWireMockStub);
        wireMockServer.stubFor(get(urlPathEqualTo("/openapi-spec"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/x-yaml")
                        .withBody(createOpenApiSpecString(" - Changed"))));
    }

    private String createOpenApiSpecString(String suffix) {
        return "openapi: 3.0.0\n" +
                "info:\n" +
                "  title: Example" +
                suffix +
                "\n" +
                "  description: Example" +
                suffix +
                "\n" +
                "  version: 0.0.0\n" +
                "paths:\n" +
                "  /example:\n" +
                "    get:\n" +
                "      summary: Example" +
                suffix +
                "\n" +
                "      description: Example" +
                suffix +
                "\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: Example" +
                suffix +
                "\n" +
                "          content:\n" +
                "            application/json:\n" +
                "              schema: \n" +
                "                type: object";
    }

    private String sanitizeErrorMessage(String message) {
        return message.replaceAll(
                "\"[^\"]*src/test/resources/tech/kronicle/plugins/openapi/OpenApiScannerTest/",
                "\"");
    }

    @SneakyThrows
    private ObjectNode getSpecAsJsonTree(OpenApiSpec openApiSpec) {
        return (ObjectNode) objectMapper.readTree(openApiSpec.getSpec());
    }

    private List<OpenApiSpec> getSpecs(Output<Void, Component> returnValue) {
        OpenApiSpecsState state = getMutatedComponentIgnoringErrors(returnValue)
                .getState(OpenApiSpecsState.TYPE);
        return state.getOpenApiSpecs();
    }

    private void assertNoState(Output<Void, Component> returnValue) {
        assertThat(getMutatedComponent(returnValue).getStates()).isEmpty();
    }
}
