package tech.kronicle.tracingprocessor.internal.services;

import org.apache.commons.lang3.RandomStringUtils;
import tech.kronicle.pluginapi.finders.models.GenericSpan;
import tech.kronicle.pluginapi.finders.models.GenericTrace;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TracingTestHelper {

    private final Random random = new Random();
    private long timestampInMicroseconds = 0;
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
        timestampInMicroseconds += TimeUnit.MILLISECONDS.toMicros(1);
        return timestampInMicroseconds;
    }

    public Long getNextDurationInMicroseconds() {
        durationInMicroseconds += TimeUnit.MILLISECONDS.toMicros(1);
        return durationInMicroseconds;
    }
}
