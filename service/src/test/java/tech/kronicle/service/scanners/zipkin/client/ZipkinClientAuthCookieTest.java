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

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"zipkin.base-url=http://localhost:36203", "zipkin.cookie-name=test-name", "zipkin.cookie-value=test-value"})
@ContextConfiguration(classes = {ZipkinClientTestConfiguration.class})
@EnableConfigurationProperties(value = { ZipkinConfig.class})
public class ZipkinClientAuthCookieTest extends BaseZipkinClientTest {

    private static final int PORT = 36203;

    @Autowired
    private ZipkinClient underTest;
    private LogCaptor logCaptor;
    private WireMockServer wireMockServer;

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
        wireMockServer = ZipkinWireMockFactory.createWithAuthCookie(PORT);

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
        return provideZipkinClientMethods(PORT);
    }
}
