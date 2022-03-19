package tech.kronicle.tracingprocessor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TracingProcessorFactoryTest {

    @Test
    public void createTracingProcessor() {
        // When
        TracingProcessor tracingProcessor = TracingProcessorFactory.createTracingProcessor();

        // Then
        assertThat(tracingProcessor).isNotNull();
    }
}
