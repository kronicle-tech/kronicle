package tech.kronicle.service.springdoc.spring;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springdoc.core.customizers.OpenApiCustomiser;
import tech.kronicle.service.springdoc.config.OpenApiSpecConfig;
import tech.kronicle.service.springdoc.config.OpenApiSpecServerConfig;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SpringdocConfigTest {

    public static final Parameter EXPECTED_FIELDS_QUERY_PARAMETER = new QueryParameter()
            .name("fields")
            .description("Supports the Partial Responses feature that Google uses on some of their APIs.  See " +
                    "https://dev.to/andersonjoseph/what-they-want-is-what-they-get-the-partial-response-strategy-5a0m" +
                    " for more information")
            .schema(new Schema<String>()
                    .type("string")
                    .minLength(1))
            .required(false);
    public static final List<OpenApiSpecServerConfig> TEST_SERVERS_CONFIG = List.of(
            new OpenApiSpecServerConfig("https://example.com/test-server-1", "Test Description 1"),
            new OpenApiSpecServerConfig("https://example.com/test-server-2", "Test Description 2"));
    public static final List<Server> TEST_SERVERS = List.of(
            new Server().url("https://example.com/test-server-1").description("Test Description 1"),
            new Server().url("https://example.com/test-server-2").description("Test Description 2"));

    @Test
    public void openApiCustomiserShouldAddInfo() {
        // Given
        SpringdocConfig underTest = new SpringdocConfig();
        OpenApiSpecConfig config = new OpenApiSpecConfig(null, null);
        OpenAPI openApi = new OpenAPI().paths(new Paths());

        // When
        OpenApiCustomiser openApiCustomiser = underTest.openApiCustomiser(config, "1.2.3");
        openApiCustomiser.customise(openApi);

        // Then
        assertThat(openApi.getInfo()).isEqualTo(new Info()
                .title("Kronicle Service")
                .description("The Kronicle Service contains all the data loaded into Kronicle via `kronicle.yaml` " +
                        "files and data pulled in from external sources via Kronicle Scanners.  All this data can " +
                        "be retrieved via the service's endpoints.  \n" +
                        "\n" +
                        "The service's endpoints support a `fields` query parameter that can be used to limit the " +
                        "JSON objects, arrays and fields that are included in JSON response bodies.  This is similar " +
                        "to GraphQL's ability to specify what fields to return in a response.  See " +
                        "https://dev.to/andersonjoseph/what-they-want-is-what-they-get-the-partial-response-strategy-5a0m " +
                        "for more information.  ")
                .version("1.2.3"));
    }

    @ParameterizedTest
    @MethodSource("provideNullAndFalse")
    public void openApiCustomiserShouldNotClearExistingServersIfClearExistingServersConfigIsNullOrFalse(Boolean clearExistingServersConfig) {
        // Given
        SpringdocConfig underTest = new SpringdocConfig();
        OpenApiSpecConfig config = new OpenApiSpecConfig(clearExistingServersConfig, null);
        OpenAPI openApi = new OpenAPI()
                .servers(TEST_SERVERS)
                .paths(new Paths());

        // When
        OpenApiCustomiser openApiCustomiser = underTest.openApiCustomiser(config, null);
        openApiCustomiser.customise(openApi);

        // Then
        assertThat(openApi.getServers()).containsExactlyElementsOf(TEST_SERVERS);
    }

    private static Stream<Boolean> provideNullAndFalse() {
        return Stream.of(null, false);
    }

    @Test
    public void openApiCustomiserShouldClearExistingServersIfClearExistingServersConfigIsTrue() {
        // Given
        SpringdocConfig underTest = new SpringdocConfig();
        OpenApiSpecConfig config = new OpenApiSpecConfig(true, null);
        OpenAPI openApi = new OpenAPI()
                .servers(TEST_SERVERS)
                .paths(new Paths());

        // When
        OpenApiCustomiser openApiCustomiser = underTest.openApiCustomiser(config, null);
        openApiCustomiser.customise(openApi);

        // Then
        assertThat(openApi.getServers()).isNull();
    }

    @Test
    public void openApiCustomiserShouldAddServersFromServersConfig() {
        // Given
        SpringdocConfig underTest = new SpringdocConfig();
        OpenApiSpecConfig config = new OpenApiSpecConfig(null, TEST_SERVERS_CONFIG);
        OpenAPI openApi = new OpenAPI().paths(new Paths());

        // When
        OpenApiCustomiser openApiCustomiser = underTest.openApiCustomiser(config, null);
        openApiCustomiser.customise(openApi);

        // Then
        assertThat(openApi.getServers()).containsExactlyElementsOf(TEST_SERVERS);
    }

    @Test
    public void openApiCustomiserShouldDoNothingWhenThereAreNoPaths() {
        // Given
        SpringdocConfig underTest = new SpringdocConfig();
        OpenApiSpecConfig config = new OpenApiSpecConfig(null, null);
        OpenAPI openApi = new OpenAPI().paths(new Paths());

        // When
        OpenApiCustomiser openApiCustomiser = underTest.openApiCustomiser(config, null);
        openApiCustomiser.customise(openApi);

        // Then
        assertThat(openApi.getPaths()).isEmpty();
    }

    @Test
    public void openApiCustomiserShouldAddFieldsQueryParameterToEveryGetOperation() {
        // Given
        SpringdocConfig underTest = new SpringdocConfig();
        OpenApiSpecConfig config = new OpenApiSpecConfig(null, null);
        OpenAPI openApi = new OpenAPI();
        openApi.path("test-path-1", new PathItem().get(new Operation()));
        openApi.path("test-path-2", new PathItem().get(new Operation()));

        // When
        OpenApiCustomiser openApiCustomiser = underTest.openApiCustomiser(config, null);
        openApiCustomiser.customise(openApi);

        // Then
        assertThat(openApi.getPaths()).hasSize(2);
        IntStream.rangeClosed(1, 2).forEach(pathNumber -> {
            List<Parameter> parameters = openApi.getPaths().get("test-path-" + pathNumber).getGet().getParameters();
            assertThat(parameters).hasSize(1);
            Parameter parameter = parameters.get(0);
            assertThat(parameter).isEqualTo(EXPECTED_FIELDS_QUERY_PARAMETER);
        });
    }

    @Test
    public void openApiCustomiserShouldAddFieldsQueryParameterToEveryGetOperationWhenOperationsAlreadyHaveParameters() {
        // Given
        SpringdocConfig underTest = new SpringdocConfig();
        OpenApiSpecConfig config = new OpenApiSpecConfig(null, null);
        OpenAPI openApi = new OpenAPI();
        openApi.path("test-path-1", new PathItem().get(
                new Operation().addParametersItem(new QueryParameter().name("test-query-parameter-1"))));
        openApi.path("test-path-2", new PathItem().get(
                new Operation().addParametersItem(new QueryParameter().name("test-query-parameter-2"))));

        // When
        OpenApiCustomiser openApiCustomiser = underTest.openApiCustomiser(config, null);
        openApiCustomiser.customise(openApi);

        // Then
        assertThat(openApi.getPaths()).hasSize(2);
        IntStream.rangeClosed(1, 2).forEach(pathNumber -> {
            List<Parameter> parameters = openApi.getPaths().get("test-path-" + pathNumber).getGet().getParameters();
            assertThat(parameters).hasSize(2);
            Parameter parameter;
            parameter = parameters.get(0);
            assertThat(parameter).isEqualTo(new QueryParameter().name("test-query-parameter-" + pathNumber));
            parameter = parameters.get(1);
            assertThat(parameter).isEqualTo(EXPECTED_FIELDS_QUERY_PARAMETER);
        });
    }

    @Test
    public void openApiCustomiserShouldNotAddFieldsQueryParameterToOtherTypesOfOperator() {
        // Given
        SpringdocConfig underTest = new SpringdocConfig();
        OpenApiSpecConfig config = new OpenApiSpecConfig(null, null);
        OpenAPI openApi = new OpenAPI();
        openApi.path("test-path-1", new PathItem().post(new Operation()).put(new Operation()));
        openApi.path("test-path-2", new PathItem().post(new Operation()).put(new Operation()));

        // When
        OpenApiCustomiser openApiCustomiser = underTest.openApiCustomiser(config, null);
        openApiCustomiser.customise(openApi);

        // Then
        assertThat(openApi.getPaths()).hasSize(2);
        IntStream.rangeClosed(1, 2).forEach(pathNumber -> {
            List<Operation> operations = openApi.getPaths().get("test-path-" + pathNumber).readOperations();
            assertThat(operations).hasSize(2);
            operations.forEach(operation -> {
                List<Parameter> parameters = operation.getParameters();
                assertThat(parameters).isNull();
            });
        });
    }

    @Test
    public void openApiCustomiserShouldDoNothingWhenServersConfigIsAnEmptyList() {
        // Given
        SpringdocConfig underTest = new SpringdocConfig();
        OpenApiSpecConfig config = new OpenApiSpecConfig(null, List.of());
        OpenAPI openApi = new OpenAPI().paths(new Paths());

        // When
        OpenApiCustomiser openApiCustomiser = underTest.openApiCustomiser(config, null);
        openApiCustomiser.customise(openApi);

        // Then
        assertThat(openApi.getServers()).isNull();
    }
}
