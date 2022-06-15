package tech.kronicle.tracingprocessor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GraphProcessorFactoryTest {

    @Test
    public void createTracingProcessor() {
        // When
        GraphProcessor graphProcessor = GraphProcessorFactory.createTracingProcessor();

        // Then
        assertThat(graphProcessor).isNotNull();
    }
}
