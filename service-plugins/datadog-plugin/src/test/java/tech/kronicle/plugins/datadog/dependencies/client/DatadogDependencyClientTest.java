package tech.kronicle.plugins.datadog.dependencies.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import tech.kronicle.plugins.datadog.config.DatadogConfig;
import tech.kronicle.plugins.datadog.dependencies.config.DatadogDependenciesConfig;
import tech.kronicle.sdk.models.Dependency;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ContextConfiguration(classes = {DatadogDependencyClientTestConfiguration.class})
public class DatadogDependencyClientTest {

    private static final String baseUrl = "http://localhost:" + DatadogApiWireMockFactory.PORT;
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final DatadogApiWireMockFactory datadogApiWireMockFactory = new DatadogApiWireMockFactory();
    private DatadogDependencyClient underTest;
    @Autowired
    private WebClient webClient;
    private WireMockServer wireMockServer;

    @AfterEach
    public void afterEach() {
        wireMockServer.stop();
    }

    @Test
    public void getDependenciesShouldReturnAllDependenciesForAllEnvironments() {
        // Given
        wireMockServer = datadogApiWireMockFactory.create();
        createClient();

        // When
        List<Dependency> returnValue = underTest.getDependencies(DatadogApiWireMockFactory.ENVIRONMENT);

        // Then
        assertThat(returnValue).containsExactlyInAnyOrder(
                new Dependency("test-service-1", "test-service-2"),
                new Dependency("test-service-1", "test-service-3"),
                new Dependency("test-service-4", "test-service-5"),
                new Dependency("test-service-4", "test-service-6")
        );
    }

    private void createClient() {
        DatadogConfig config = new DatadogConfig(baseUrl, DatadogApiWireMockFactory.API_KEY, DatadogApiWireMockFactory.APPLICATION_KEY, null);
        DatadogDependenciesConfig dependenciesConfig = new DatadogDependenciesConfig(TIMEOUT, null);
        underTest = new DatadogDependencyClient(webClient, config, dependenciesConfig);
    }
}
