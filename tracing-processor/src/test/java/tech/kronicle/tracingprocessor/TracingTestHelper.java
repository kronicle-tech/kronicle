package tech.kronicle.tracingprocessor;

import org.apache.commons.lang3.RandomStringUtils;
import tech.kronicle.pluginapi.finders.models.GenericSpan;
import tech.kronicle.pluginapi.finders.models.GenericTrace;
import tech.kronicle.sdk.models.SummaryComponentDependencyDuration;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TracingTestHelper {

    private static final LocalDateTime INITIAL_TIMESTAMP = LocalDateTime.of(2021, 1, 1, 0, 0);
    private static final long INITIAL_TIMESTAMP_IN_MICROSECONDS = toEpochMicroseconds(INITIAL_TIMESTAMP);

    private final Random random = new Random();
    private long timestampInMicroseconds = INITIAL_TIMESTAMP_IN_MICROSECONDS;
    private long durationInMicroseconds = 0;

    public static GenericTrace createTrace(GenericSpan... spans) {
        return new GenericTrace(List.of(spans));
    }

    public GenericSpan.GenericSpanBuilder spanBuilder() {
        return GenericSpan.builder()
                .id(randomString())
                .name(randomString())
                .timestamp(random.nextLong())
                .duration(random.nextLong())
                .sourceName(randomString());
    }

    private String randomString() {
        return RandomStringUtils.random(20);
    }

    public long getNextTimestampInMicroseconds() {
        timestampInMicroseconds += TimeUnit.SECONDS.toMicros(1);
        return timestampInMicroseconds;
    }

    public Long getNextDurationInMicroseconds() {
        durationInMicroseconds += TimeUnit.MILLISECONDS.toMicros(1);
        return durationInMicroseconds;
    }

    public LocalDateTime getTimestamp(int additionalSeconds) {
        return INITIAL_TIMESTAMP.plusSeconds(additionalSeconds);
    }

    public SummaryComponentDependencyDuration createDuration(long minInMicroseconds, long maxInMicroseconds, long p50InMicroseconds,
            long p90InMicroseconds, long p99InMicroseconds, long p99Point9InMicroseconds) {
        return new SummaryComponentDependencyDuration(
                minInMicroseconds,
                maxInMicroseconds,
                p50InMicroseconds,
                p90InMicroseconds,
                p99InMicroseconds,
                p99Point9InMicroseconds);
    }

    private static long toEpochMicroseconds(LocalDateTime value) {
        Instant instant = value.toInstant(ZoneOffset.UTC);
        return instant.getEpochSecond() * 1000_000 + instant.getNano() / 1000;
    }
}
