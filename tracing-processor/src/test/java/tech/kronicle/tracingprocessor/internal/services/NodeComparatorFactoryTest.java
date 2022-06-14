package tech.kronicle.tracingprocessor.internal.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.GraphNode;
import tech.kronicle.sdk.models.Tag;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NodeComparatorFactoryTest {

    @Test
    public void componentNodeComparatorShouldReturnMinusOneWhenComponentId1IsLessThanComponentId2() {
        // Given
        GraphNode object1 = GraphNode.builder().componentId("a").build();
        GraphNode object2 = GraphNode.builder().componentId("b").build();

        // When
        int returnValue = NodeComparators.COMPONENT_NODE_COMPARATOR.compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(-1);
    }

    @Test
    public void componentNodeComparatorShouldReturnZeroWhenComponentId1IsEqualToComponentId2() {
        // Given
        GraphNode object1 = GraphNode.builder().componentId("a").build();
        GraphNode object2 = GraphNode.builder().componentId("a").build();

        // When
        int returnValue = NodeComparators.COMPONENT_NODE_COMPARATOR.compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(0);
    }

    @Test
    public void componentNodeComparatorShouldReturnOneWhenComponentId1IsGreaterThanComponentId2() {
        // Given
        GraphNode object1 = GraphNode.builder().componentId("b").build();
        GraphNode object2 = GraphNode.builder().componentId("a").build();

        // When
        int returnValue = NodeComparators.COMPONENT_NODE_COMPARATOR.compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(1);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnMinusOneWhenComponentId1IsLessThanComponentId2() {
        // Given
        GraphNode object1 = GraphNode.builder().componentId("a").build();
        GraphNode object2 = GraphNode.builder().componentId("b").build();

        // When
        int returnValue = NodeComparators.SUB_COMPONENT_NODE_COMPARATOR.compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(-1);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnZeroWhenComponentId1IsEqualToComponentId2() {
        // Given
        GraphNode object1 = GraphNode.builder().componentId("a").build();
        GraphNode object2 = GraphNode.builder().componentId("a").build();

        // When
        int returnValue = NodeComparators.SUB_COMPONENT_NODE_COMPARATOR.compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(0);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnOneWhenComponentId1IsGreaterThanComponentId2() {
        // Given
        GraphNode object1 = GraphNode.builder().componentId("b").build();
        GraphNode object2 = GraphNode.builder().componentId("a").build();

        // When
        int returnValue = NodeComparators.SUB_COMPONENT_NODE_COMPARATOR.compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(1);
    }

    @Test
    public void subComponentNodeComparatorShouldIgnoreNamesWhenComponentIdsAreDifferent() {
        // Given
        GraphNode object1 = GraphNode.builder().componentId("a").name("b").build();
        GraphNode object2 = GraphNode.builder().componentId("b").name("a").build();

        // When
        int returnValue = NodeComparators.SUB_COMPONENT_NODE_COMPARATOR.compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(-1);
    }

    @Test
    public void subComponentNodeComparatorShouldIgnoreTagsWhenComponentIdsAreDifferent() {
        // Given
        GraphNode object1 = GraphNode.builder()
                .componentId("a")
                .tags(List.of(new Tag("b", "b")))
                .build();
        GraphNode object2 = GraphNode.builder()
                .componentId("b")
                .tags(List.of(new Tag("a", "a")))
                .build();

        // When
        int returnValue = NodeComparators.SUB_COMPONENT_NODE_COMPARATOR.compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(-1);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnMinusOneWhenName1IsLessThanName2() {
        // Given
        GraphNode object1 = GraphNode.builder().componentId("a").name("a").build();
        GraphNode object2 = GraphNode.builder().componentId("a").name("b").build();

        // When
        int returnValue = NodeComparators.SUB_COMPONENT_NODE_COMPARATOR.compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(-1);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnZeroWhenName1IsEqualToName2() {
        // Given
        GraphNode object1 = GraphNode.builder().componentId("a").name("a").build();
        GraphNode object2 = GraphNode.builder().componentId("a").name("a").build();

        // When
        int returnValue = NodeComparators.SUB_COMPONENT_NODE_COMPARATOR.compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(0);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnOneWhenName1IsGreaterThanName2() {
        // Given
        GraphNode object1 = GraphNode.builder().componentId("a").name("b").build();
        GraphNode object2 = GraphNode.builder().componentId("a").name("a").build();

        // When
        int returnValue = NodeComparators.SUB_COMPONENT_NODE_COMPARATOR.compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(1);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnMinusOneWhenName1IsNullAndName2IsNotNull() {
        // Given
        GraphNode object1 = GraphNode.builder().componentId("a").name(null).build();
        GraphNode object2 = GraphNode.builder().componentId("a").name("a").build();

        // When
        int returnValue = NodeComparators.SUB_COMPONENT_NODE_COMPARATOR.compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(-1);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnZeroWhenName1AndName2AreBothNull() {
        // Given
        GraphNode object1 = GraphNode.builder().componentId("a").name(null).build();
        GraphNode object2 = GraphNode.builder().componentId("a").name(null).build();

        // When
        int returnValue = NodeComparators.SUB_COMPONENT_NODE_COMPARATOR.compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(0);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnOneWhenName1IsNotNullAndName2IsNull() {
        // Given
        GraphNode object1 = GraphNode.builder().componentId("a").name("a").build();
        GraphNode object2 = GraphNode.builder().componentId("a").name(null).build();

        // When
        int returnValue = NodeComparators.SUB_COMPONENT_NODE_COMPARATOR.compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(1);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnMinusOneWhenTags1IsLessThanTags2() {
        // Given
        GraphNode object1 = GraphNode.builder()
                .componentId("a")
                .name("a")
                .tags(List.of(new Tag("a", "a")))
                .build();
        GraphNode object2 = GraphNode.builder()
                .componentId("a")
                .name("a")
                .tags(List.of(new Tag("b", "b")))
                .build();

        // When
        int returnValue = NodeComparators.SUB_COMPONENT_NODE_COMPARATOR.compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(-1);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnZeroWhenTags1IsEqualToTags2() {
        // Given
        GraphNode object1 = GraphNode.builder()
                .componentId("a")
                .name("a")
                .tags(List.of(new Tag("a", "a")))
                .build();
        GraphNode object2 = GraphNode.builder()
                .componentId("a")
                .name("a")
                .tags(List.of(new Tag("a", "a")))
                .build();

        // When
        int returnValue = NodeComparators.SUB_COMPONENT_NODE_COMPARATOR.compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(0);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnOneWhenTags1IsGreaterThanTags2() {
        // Given
        GraphNode object1 = GraphNode.builder()
                .componentId("a")
                .name("a")
                .tags(List.of(new Tag("b", "b")))
                .build();
        GraphNode object2 = GraphNode.builder()
                .componentId("a")
                .name("a")
                .tags(List.of(new Tag("a", "a")))
                .build();

        // When
        int returnValue = NodeComparators.SUB_COMPONENT_NODE_COMPARATOR.compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(1);
    }

    @Test
    public void subComponentNodeComparatorShouldIgnoreTagsWhenNamesAreDifferent() {
        // Given
        GraphNode object1 = GraphNode.builder()
                .componentId("a")
                .name("a")
                .tags(List.of(new Tag("b", "b")))
                .build();
        GraphNode object2 = GraphNode.builder()
                .componentId("a")
                .name("b")
                .tags(List.of(new Tag("a", "a")))
                .build();

        // When
        int returnValue = NodeComparators.SUB_COMPONENT_NODE_COMPARATOR.compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(-1);
    }
}
