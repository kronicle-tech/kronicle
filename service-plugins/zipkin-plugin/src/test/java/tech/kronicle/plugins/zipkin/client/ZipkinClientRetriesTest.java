package tech.kronicle.plugins.zipkin.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tech.kronicle.plugins.zipkin.ZipkinWireMockFactory;
import tech.kronicle.plugintestutils.testutils.LogCaptor;
import tech.kronicle.plugintestutils.testutils.Timer;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ZipkinClientRetriesTest extends BaseZipkinClientTest {

    private static final int PORT = 36205;

    private final ZipkinClient underTest = zipkinClient(PORT, Duration.ofMillis(1));
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
