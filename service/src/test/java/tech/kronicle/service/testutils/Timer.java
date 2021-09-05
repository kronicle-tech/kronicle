package tech.kronicle.service.testutils;

import java.time.Duration;
import java.time.Instant;

public class Timer {

    private final Instant startTimestamp = Instant.now();
    private Instant endTimestamp;

    public void stop() {
        endTimestamp = Instant.now();
    }

    public int getDurationInSeconds() {
        return (int) Duration.between(startTimestamp, endTimestamp).toSeconds();
    }
}
