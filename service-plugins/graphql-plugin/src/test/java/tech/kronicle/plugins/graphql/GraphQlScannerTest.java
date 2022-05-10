package tech.kronicle.plugins.graphql;

import com.github.tomakehurst.wiremock.WireMockServer;
import graphql.introspection.IntrospectionResultToSchema;
import graphql.schema.idl.SchemaPrinter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.ComponentAndCodebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.graphql.config.GraphQlConfig;
import tech.kronicle.plugins.graphql.services.SchemaDownloader;
import tech.kronicle.plugins.graphql.services.SchemaFetcher;
import tech.kronicle.plugins.graphql.services.SchemaFileReader;
import tech.kronicle.plugins.graphql.services.SchemaTransformer;
import tech.kronicle.plugintestutils.scanners.BaseCodebaseScannerTest;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.graphql.GraphQlSchema;
import tech.kronicle.utils.ThrowableToScannerErrorMapper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static graphql.introspection.IntrospectionQuery.INTROSPECTION_QUERY;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.utils.FileUtilsFactory.createFileUtils;
import static tech.kronicle.utils.HttpClientFactory.createHttpClient;
import static tech.kronicle.utils.JsonMapperFactory.createJsonMapper;

public class GraphQlScannerTest extends BaseCodebaseScannerTest {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);
    private static final String URL_INTROSPECTION_RESULT = "{\n" +
            "  \"data\": {\n" +
            "    \"__schema\": {\n" +
            "      \"queryType\": {\n" +
            "        \"name\": \"Query\"\n" +
            "      },\n" +
            "      \"types\": [\n" +
            "        {\n" +
            "          \"kind\": \"OBJECT\",\n" +
            "          \"name\": \"Query\",\n" +
            "          \"fields\": [\n" +
            "            {\n" +
            "              \"name\": \"urlGreet\",\n" +
            "              \"args\": [],\n" +
            "              \"type\": {\n" +
            "                \"kind\": \"OBJECT\",\n" +
            "                \"name\": \"Greeting\"\n" +
            "              },\n" +
            "              \"isDeprecated\": false\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"kind\": \"OBJECT\",\n" +
            "          \"name\": \"Greeting\",\n" +
            "          \"fields\": [\n" +
            "            {\n" +
            "              \"name\": \"greeting\",\n" +
            "              \"args\": [],\n" +
            "              \"type\": {\n" +
            "                \"kind\": \"SCALAR\",\n" +
            "                \"name\": \"String\"\n" +
            "              },\n" +
            "              \"isDeprecated\": false\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  }\n" +
            "}";
    private static final String URL_SCHEMA = "\"Marks the field, argument, input field or enum value as deprecated\"\n" +
            "directive @deprecated(\n" +
            "    \"The reason for the deprecation\"\n" +
            "    reason: String = \"No longer supported\"\n" +
            "  ) on FIELD_DEFINITION | ARGUMENT_DEFINITION | ENUM_VALUE | INPUT_FIELD_DEFINITION\n" +
            "\n" +
            "\"Directs the executor to include this field or fragment only when the `if` argument is true\"\n" +
            "directive @include(\n" +
            "    \"Included when true.\"\n" +
            "    if: Boolean!\n" +
            "  ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT\n" +
            "\n" +
            "\"Directs the executor to skip this field or fragment when the `if`'argument is true.\"\n" +
            "directive @skip(\n" +
            "    \"Skipped when true.\"\n" +
            "    if: Boolean!\n" +
            "  ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT\n" +
            "\n" +
            "\"Exposes a URL that specifies the behaviour of this scalar.\"\n" +
            "directive @specifiedBy(\n" +
            "    \"The URL that specifies the behaviour of this scalar.\"\n" +
            "    url: String!\n" +
            "  ) on SCALAR\n" +
            "\n" +
            "type Greeting {\n" +
            "  greeting: String\n" +
            "}\n" +
            "\n" +
            "type Query {\n" +
            "  urlGreet: Greeting\n" +
            "}\n";
    private static final String FILE_SCHEMA = "type Greeting {\n" +
            "    greeting: String\n" +
            "}\n" +
            "\n" +
            "type Query {\n" +
            "    fileGreet(): Greeting\n" +
            "}\n";
    private static final String GRAPHQL_URL_PATH = "/graphql-schema";
    private static final String GRAPHQL_SCHEMA_FILE_NAME = "example.graphql";

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
        assertThat(returnGraphQlSchemas).hasSize(1);
        assertThat(returnGraphQlSchemas.get(0).getSchema()).isEqualTo(FILE_SCHEMA);
        assertThat(returnGraphQlSchemas).containsExactly(graphQlSchema.withSchema(FILE_SCHEMA));
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
        assertThat(returnGraphQlSchemas).hasSize(1);
        assertThat(returnGraphQlSchemas.get(0).getSchema()).isEqualTo(URL_SCHEMA);
        assertThat(returnGraphQlSchemas).containsExactly(graphQlSchema.withSchema(URL_SCHEMA));
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
        assertThat(returnGraphQlSchemas).hasSize(2);
        assertThat(returnGraphQlSchemas.get(0).getSchema()).isEqualTo(FILE_SCHEMA);
        assertThat(returnGraphQlSchemas.get(1).getSchema()).isEqualTo(URL_SCHEMA);
        assertThat(returnGraphQlSchemas).containsExactly(
                fileGraphQlSchema.withSchema(FILE_SCHEMA),
                urlGraphQlSchema.withSchema(URL_SCHEMA)
        );
    }

    private GraphQlScanner createUnderTest() {
        Duration timeout = Duration.ofMinutes(1);
        return new GraphQlScanner(
                new SchemaFetcher(
                        new SchemaFileReader(createFileUtils()),
                        new SchemaDownloader(
                                new GraphQlConfig(timeout),
                                createHttpClient(timeout)
                        ),
                        new SchemaTransformer(
                                createJsonMapper(),
                                new IntrospectionResultToSchema(),
                                new SchemaPrinter()
                        ),
                        new ThrowableToScannerErrorMapper()
                )
        );
    }

    private void createWireMockServer() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.stubFor(post(urlPathEqualTo(GRAPHQL_URL_PATH))
                .withRequestBody(equalTo(INTROSPECTION_QUERY))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/graphql")
                        .withBody(URL_INTROSPECTION_RESULT)));
        wireMockServer.start();
    }
}
