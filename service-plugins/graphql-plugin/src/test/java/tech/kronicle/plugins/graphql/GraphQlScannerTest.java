package tech.kronicle.plugins.graphql;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.ComponentAndCodebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.graphql.config.GraphQlConfig;
import tech.kronicle.plugins.graphql.services.SchemaDownloader;
import tech.kronicle.plugins.graphql.services.SchemaFetcher;
import tech.kronicle.plugintestutils.scanners.BaseCodebaseScannerTest;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.graphql.GraphQlSchema;
import tech.kronicle.utils.ThrowableToScannerErrorMapper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.utils.FileUtilsFactory.createFileUtils;
import static tech.kronicle.utils.HttpClientFactory.createHttpClient;

public class GraphQlScannerTest extends BaseCodebaseScannerTest {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);
    public static final String URL_SCHEMA_TEXT = "type Greeting {\n" +
            "    greeting: String\n" +
            "}\n" +
            "\n" +
            "type Query {\n" +
            "    urlGreet(): Greeting\n" +
            "}\n";
    public static final String FILE_SCHEMA_TEXT = "type Greeting {\n" +
            "    greeting: String\n" +
            "}\n" +
            "\n" +
            "type Query {\n" +
            "    fileGreet(): Greeting\n" +
            "}\n";
    public static final String GRAPHQL_URL_PATH = "/graphql-schema";
    public static final String GRAPHQL_SCHEMA_FILE_NAME = "example.graphql";

    private WireMockServer wireMockServer;

    @AfterEach
    public void afterEach() {
        if (nonNull(wireMockServer)) {
            wireMockServer.stop();
        }
    }

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // Given
        GraphQlScanner underTest = createUnderTest();

        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("graphql");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // Given
        GraphQlScanner underTest = createUnderTest();

        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Loads GraphQL schemas for components so they can be rendered in Kronicle");
    }

    @Test
    public void notesShouldReturnNull() {
        // Given
        GraphQlScanner underTest = createUnderTest();

        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isNull();
    }
    
    @Test
    public void scanShouldHandleNoGraphQlSchemas() {
        // Given
        Component component = Component.builder().build();
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("NoGraphQlSchemas"));
        ComponentAndCodebase componentAndCodebase = new ComponentAndCodebase(component, codebase);
        GraphQlScanner underTest = createUnderTest();

        // When
        Output<Void, Component> returnValue = underTest.scan(componentAndCodebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        List<GraphQlSchema> graphQlSchemas = getMutatedComponent(returnValue).getGraphQlSchemas();
        assertThat(graphQlSchemas).isEmpty();
    }

    @Test
    public void scanShouldHandleCodebaseGraphQlSchema() {
        // Given
        GraphQlSchema graphQlSchema = GraphQlSchema.builder()
                .file("example.graphql")
                .build();
        Component component = Component.builder()
                .graphQlSchemas(List.of(graphQlSchema))
                .build();
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("OneGraphQlSchema"));
        ComponentAndCodebase componentAndCodebase = new ComponentAndCodebase(component, codebase);
        GraphQlScanner underTest = createUnderTest();

        // When
        Output<Void, Component> returnValue = underTest.scan(componentAndCodebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        List<GraphQlSchema> returnGraphQlSchemas = new ArrayList<>(getMutatedComponent(returnValue).getGraphQlSchemas());
        assertThat(returnGraphQlSchemas).containsExactly(graphQlSchema.withSchema(FILE_SCHEMA_TEXT));
    }

    @Test
    public void scanShouldHandleUrlGraphQlSchema() {
        // Given
        createWireMockServer();
        String graphQlSchemaUrl = wireMockServer.baseUrl() + GRAPHQL_URL_PATH;
        GraphQlSchema graphQlSchema = GraphQlSchema.builder()
                .url(graphQlSchemaUrl)
                .build();
        Component component = Component.builder()
                .graphQlSchemas(List.of(graphQlSchema))
                .build();
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("NoGraphQlSchemas"));
        ComponentAndCodebase componentAndCodebase = new ComponentAndCodebase(component, codebase);
        GraphQlScanner underTest = createUnderTest();

        // When
        Output<Void, Component> returnValue = underTest.scan(componentAndCodebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        List<GraphQlSchema> returnGraphQlSchemas = getMutatedComponent(returnValue).getGraphQlSchemas();
        assertThat(returnGraphQlSchemas).containsExactly(graphQlSchema.withSchema(URL_SCHEMA_TEXT));
    }

    @Test
    public void scanShouldHandleUrlAndCodebaseGraphQlSchemas() {
        // Given
        createWireMockServer();
        String graphQlSchemaUrl = wireMockServer.baseUrl() + GRAPHQL_URL_PATH;
        GraphQlSchema fileGraphQlSchema = GraphQlSchema.builder()
                .file(GRAPHQL_SCHEMA_FILE_NAME)
                .build();
        GraphQlSchema urlGraphQlSchema = GraphQlSchema.builder()
                .url(graphQlSchemaUrl)
                .build();
        Component component = Component.builder()
                .graphQlSchemas(List.of(
                        fileGraphQlSchema,
                        urlGraphQlSchema
                ))
                .build();
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("OneGraphQlSchema"));
        ComponentAndCodebase componentAndCodebase = new ComponentAndCodebase(component, codebase);
        GraphQlScanner underTest = createUnderTest();

        // When
        Output<Void, Component> returnValue = underTest.scan(componentAndCodebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        List<GraphQlSchema> returnGraphQlSchemas = getMutatedComponent(returnValue).getGraphQlSchemas();
        assertThat(returnGraphQlSchemas).containsExactly(
                fileGraphQlSchema.withSchema(FILE_SCHEMA_TEXT),
                urlGraphQlSchema.withSchema(URL_SCHEMA_TEXT)
        );
    }

    private GraphQlScanner createUnderTest() {
        Duration timeout = Duration.ofMinutes(1);
        return new GraphQlScanner(
                new SchemaFetcher(
                        createFileUtils(),
                        new SchemaDownloader(
                                new GraphQlConfig(timeout),
                                createHttpClient(timeout)
                        ),
                        new ThrowableToScannerErrorMapper()
                )
        );
    }

    private void createWireMockServer() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.stubFor(get(urlPathEqualTo(GRAPHQL_URL_PATH))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/graphql")
                        .withBody(URL_SCHEMA_TEXT)));
        wireMockServer.start();
    }
}
