package tech.kronicle.sdk.models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SummaryCallGraphTest {

    @Test
    public void constructorShouldSetAllFields() {
        // Given
        List<SummarySubComponentDependencyNode> nodes = List.of(SummarySubComponentDependencyNode.builder().build());
        List<SummaryComponentDependency> dependencies = List.of(SummaryComponentDependency.builder().build());
        int traceCount = 1;

        // When
        SummaryCallGraph underTest = new SummaryCallGraph(nodes, dependencies, traceCount);

        // Then
        assertThat(underTest.getNodes()).isEqualTo(nodes);
        assertThat(underTest.getDependencies()).isEqualTo(dependencies);
        assertThat(underTest.getTraceCount()).isEqualTo(traceCount);
    }
}
