package tech.kronicle.service.repositories;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import tech.kronicle.plugintestutils.testutils.LogCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class RefreshingRepositoryTest {

    @Test
    public void initializeShouldTriggerARefresh() {
        // Given
        TestRefreshingRepository underTest = new TestRefreshingRepository(false, false);
        assertThat(underTest.getRefreshCount()).isEqualTo(0);

        // When
        underTest.initialize();

        // Then
        await().atMost(10, SECONDS).until(() -> underTest.getRefreshCount() == 1);
        assertThat(underTest.getRefreshFirstTimeValues()).containsExactly(true);
    }

    @Test
    public void initializeShouldCallDoInitialize() {
        // Given
        TestRefreshingRepository underTest = new TestRefreshingRepository(false, false);
        assertThat(underTest.getInitializeCount()).isEqualTo(0);

        // When
        underTest.initialize();

        // Then
        await().atMost(10, SECONDS).until(() -> underTest.getInitializeCount() == 1);
    }

    @Test
    public void refreshShouldPreventConcurrentRefreshes() throws InterruptedException {
        // Given
        TestRefreshingRepository underTest = new TestRefreshingRepository(true, false);
        assertThat(underTest.getRefreshCount()).isEqualTo(0);

        // When
        Thread thread1 = new Thread(underTest::refresh);
        Thread thread2 = new Thread(underTest::refresh);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        // Then
        assertThat(underTest.getRefreshCount()).isEqualTo(1);
        assertThat(underTest.getRefreshFirstTimeValues()).containsExactly(false);
    }

    @Test
    public void refreshShouldCatchAndLogAnException() {
        // Given
        TestRefreshingRepository underTest = new TestRefreshingRepository(false, true);
        assertThat(underTest.getRefreshCount()).isEqualTo(0);
        LogCaptor logCaptor = new LogCaptor(underTest.getClass());

        // When
        underTest.refresh();

        // Then
        assertThat(underTest.getRefreshCount()).isEqualTo(1);
        assertThat(underTest.getRefreshFirstTimeValues()).containsExactly(false);
        assertThat(logCaptor.getEvents()).hasSize(2);
        assertLogEvent(logCaptor, 0, Level.INFO, "Starting refresh");
        ILoggingEvent event = assertLogEvent(logCaptor, 1, Level.ERROR, "Refresh aborted due to exception");
        IThrowableProxy throwableProxy = event.getThrowableProxy();
        assertThat(throwableProxy).isNotNull();
        assertThat(throwableProxy.getClassName()).isEqualTo("java.lang.RuntimeException");
        assertThat(throwableProxy.getMessage()).isEqualTo("Refresh Exception");
        logCaptor.close();
    }

    private ILoggingEvent assertLogEvent(LogCaptor logCaptor, int index, Level level, String message) {
        ILoggingEvent event = logCaptor.getEvents().get(index);
        assertThat(event.getLevel()).isEqualTo(level);
        assertThat(event.getMessage()).isEqualTo(message);
        return event;
    }

    @RequiredArgsConstructor
    @Slf4j
    private static final class TestRefreshingRepository extends RefreshingRepository {

        private final AtomicInteger initializeCount = new AtomicInteger();
        private final AtomicInteger refreshCount = new AtomicInteger();
        private final List<Boolean> refreshFirstTimeValues = new ArrayList<>();
        private final boolean sleepInRefresh;
        private final boolean exceptionInRefresh;

        public int getInitializeCount() {
            return initializeCount.get();
        }

        public int getRefreshCount() {
            return refreshCount.get();
        }

        public List<Boolean> getRefreshFirstTimeValues() {
            return List.copyOf(refreshFirstTimeValues);
        }

        @Override
        protected void doInitialize() {
            initializeCount.incrementAndGet();
        }

        @Override
        protected void doRefresh(boolean firstTime) {
            refreshFirstTimeValues.add(firstTime);
            refreshCount.incrementAndGet();
            if (exceptionInRefresh) {
                throw new RuntimeException("Refresh Exception");
            } else if (sleepInRefresh) {
                try {
                    Thread.sleep(SECONDS.toMillis(10));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        protected Logger log() {
            return log;
        }
    }
}
