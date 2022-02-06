package tech.kronicle.service.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.kronicle.service.testutils.Timer;
import tech.kronicle.service.utils.ObjectReference;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"resilience4j.retry.instances.http-request-maker.waitDuration=1ms"})
@ContextConfiguration(classes = { HttpRequestMakerTestConfiguration.class})
public class HttpRequestMakerTest {

    @Autowired
    private HttpRequestMaker underTest;

    @Test
    public void makeHttpRequestShouldRetryWhenHttpRequestThrowsAnException() {
        // Given
        ObjectReference<Integer> counter = new ObjectReference<>(0);
        Function<String, ResponseEntity<String>> httpRequest = ignored -> {
            counter.set(counter.get() + 1);
            throw new RuntimeException("test");
        };

        // When
        Timer timer = new Timer();
        Throwable thrown = catchThrowable(() -> underTest.makeHttpRequest(httpRequest, "https://example.com"));
        timer.stop();

        // Then
        assertThat(thrown).isInstanceOf(RuntimeException.class);
        assertThat(thrown).hasMessage("test");
        assertThat(counter.get()).isEqualTo(5);
        ensureRetriesDoNotSlowDownTestExecution(timer);
    }

    private void ensureRetriesDoNotSlowDownTestExecution(Timer timer) {
        // The retries should execute quickly due to waitDuration config being overridden via @SpringBootTest annotation on this class
        assertThat(timer.getDurationInSeconds()).isLessThan(10);
    }
}
