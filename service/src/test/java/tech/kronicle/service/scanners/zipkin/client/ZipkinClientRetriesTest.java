package tech.kronicle.service.scanners.zipkin.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import tech.kronicle.service.scanners.zipkin.ZipkinWireMockFactory;
import tech.kronicle.service.scanners.zipkin.config.ZipkinConfig;
import tech.kronicle.service.testutils.LogCaptor;
import tech.kronicle.service.testutils.Timer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"zipkin.base-url=http://localhost:36205", "resilience4j.retry.instances.zipkin-client.waitDuration=1ms"})
@ContextConfiguration(classes = {ZipkinClientTestConfiguration.class})
@EnableConfigurationProperties(value = {ZipkinConfig.class})
public class ZipkinClientRetriesTest extends BaseZipkinClientTest {

    private static final int PORT = 36205;

    @Autowired
    private ZipkinClient underTest;
    private LogCaptor logCaptor;
    private WireMockServer wireMockServer;
    private ZipkinWireMockFactory zipkinWireMockFactory = new ZipkinWireMockFactory();;

    @BeforeEach
    public void beforeEach() {
        logCaptor = new LogCaptor(ZipkinClient.class);
    }

    @AfterEach
    public void afterEach() {
        logCaptor.close();
        wireMockServer.stop();
    }

    @ParameterizedTest()
    @MethodSource("provideZipkinClientMethods")
    public void zipkinClientMethodShouldRetryFailures(ZipkinClientMethod zipkinClientMethod) throws URISyntaxException {
        // Given
        wireMockServer = zipkinWireMockFactory.createWithErrorResponses(PORT);

        // When
        Timer timer = new Timer();
        Throwable thrown = catchThrowable(() -> zipkinClientMethod.getMethod().apply(underTest));
        timer.stop();

        // Then
        assertThat(thrown).isInstanceOf(ZipkinClientException.class);
        assertThat(thrown).hasMessageContaining("Call to '" + zipkinClientMethod.getUrl() + "' failed with status 500");
        String path = new URI(zipkinClientMethod.getUrl()).getPath();
        wireMockServer.verify(10, getRequestedFor(urlPathEqualTo(path)));
        assertLogEvents(logCaptor, zipkinClientMethod.getUrl());
        ensureRetriesDoNotSlowDownTestExecution(timer);
    }

    protected static Stream<ZipkinClientMethod> provideZipkinClientMethods() {
        return provideZipkinClientMethods(PORT);
    }

    private void ensureRetriesDoNotSlowDownTestExecution(Timer timer) {
        // The retries should execute quickly due to waitDuration config being overridden via @SpringBootTest annotation on this class
        assertThat(timer.getDurationInSeconds()).isLessThan(10);
    }
}
