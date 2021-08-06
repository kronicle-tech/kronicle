package com.moneysupermarket.componentcatalog.service.scanners.zipkin.testutils;

import com.moneysupermarket.componentcatalog.sdk.models.SummaryComponentDependencyDuration;
import com.moneysupermarket.componentcatalog.service.scanners.zipkin.models.api.Endpoint;
import com.moneysupermarket.componentcatalog.service.scanners.zipkin.models.api.Span;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ZipkinApiModelTestHelper {

    private static final LocalDateTime INITIAL_TIMESTAMP = LocalDateTime.of(2021, 1, 1, 0, 0);
    private static final long INITIAL_TIMESTAMP_IN_MICROSECONDS = toEpochMicroseconds(INITIAL_TIMESTAMP);

    private final Random random = new Random();
    private long timestampInMicroseconds = INITIAL_TIMESTAMP_IN_MICROSECONDS;
    private long durationInMicroseconds = 0;

    public Span.SpanBuilder createTestSpanBuilder() {
        return Span.builder()
                .traceId(randomHexString(32))
                .id(randomHexString(16))
                .name(RandomStringUtils.random(20))
                .timestamp(random.nextLong())
                .duration(random.nextLong())
                .localEndpoint(createTestEndpointBuilder().build());
    }

    public Endpoint.EndpointBuilder createTestEndpointBuilder() {
        return Endpoint.builder()
                .serviceName(RandomStringUtils.random(20))
                .ipv4(RandomStringUtils.random(20))
                .ipv6(RandomStringUtils.random(20))
                .port(random.nextInt());
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

    private String randomHexString(int length) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, length);
    }

    private static long toEpochMicroseconds(LocalDateTime value) {
        Instant instant = value.toInstant(ZoneOffset.UTC);
        return instant.getEpochSecond() * 1000_000 + instant.getNano() / 1000;
    }
}
