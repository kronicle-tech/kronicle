package tech.kronicle.plugins.zipkin.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tech.kronicle.plugins.zipkin.ZipkinWireMockFactory;
import tech.kronicle.testutils.LogCaptor;
import tech.kronicle.testutils.Timer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

public class ZipkinClientAuthCookieTest extends BaseZipkinClientTest {

    private static final int PORT = 36203;

    private final ZipkinClient underTest = zipkinClient(PORT, "test-name", "test-value");
    private LogCaptor logCaptor;
    private WireMockServer wireMockServer;
    private final ZipkinWireMockFactory zipkinWireMockFactory = new ZipkinWireMockFactory();

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
    public void zipkinClientMethodShouldPassAuthCookie(ZipkinClientRetriesTest.ZipkinClientMethod zipkinClientMethod) throws URISyntaxException {
        // Given
        wireMockServer = zipkinWireMockFactory.createWithAuthCookie(PORT);

        // When
        Timer timer = new Timer();
        Object returnValue = zipkinClientMethod.getMethod().apply(underTest);
        timer.stop();

        // Then
        assertThat(returnValue).isNotNull();
        String path = new URI(zipkinClientMethod.getUrl()).getPath();
        wireMockServer.verify(1, getRequestedFor(urlPathEqualTo(path)));
        assertThat(logCaptor.getEvents()).isEmpty();
    }

    protected static Stream<ZipkinClientMethod> provideZipkinClientMethods() {
        return baseProvideZipkinClientMethods(PORT);
    }
}
