package tech.kronicle.service.partialresponse.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pressassociation.pr.match.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PartialResponseApplierTest {

    private PartialResponseApplier underTest;
    private ObjectMapper objectMapper;
    private Matcher matcher;

    @BeforeEach
    public void beforeEach() {
        objectMapper = new ObjectMapper();

        underTest = new PartialResponseApplier();
    }

    @Test
    public void applyShouldFilterAnObjectNode() {
        // Given
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("testName", "testValue");
        objectNode.put("testName2", "testValue");
        matcher = Matcher.of("testName");

        // When
        underTest.apply(objectNode, matcher);

        // Then
        assertThat(objectNode.has("testName")).isTrue();
        assertThat(objectNode.has("testName2")).isFalse();
    }

    @Test
    public void applyShouldFilterAnArrayNode() {
        // Given
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("testName", "testValue");
        objectNode.put("testName2", "testValue");
        ObjectNode objectNode2 = objectMapper.createObjectNode();
        objectNode2.put("testName", "testValue");
        objectNode2.put("testName3", "testValue");
        ArrayNode arrayNode = objectMapper.createArrayNode();
        arrayNode.add(objectNode);
        arrayNode.add(objectNode2);
        matcher = Matcher.of("testName,testName3");

        // When
        underTest.apply(objectNode, matcher);

        // Then
        assertThat(arrayNode).hasSize(2);
        assertThat(arrayNode.get(0).has("testName")).isTrue();
        assertThat(arrayNode.get(0).has("testName2")).isFalse();
        assertThat(arrayNode.get(1).has("testName")).isTrue();
        assertThat(arrayNode.get(1).has("testName3")).isTrue();
    }

    @Test
    public void applyShouldFilterAnObjectNodeNestedUnderAnObjectNodeWhenFilterAppliesToParent() {
        // Given
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("testName", "testValue");
        objectNode.put("testName2", "testValue");
        ObjectNode objectNode2 = objectMapper.createObjectNode();
        objectNode.put("testName", "testValue");
        objectNode.put("testName2", "testValue");
        objectNode.put("child", objectNode2);

        matcher = Matcher.of("testName");

        // When
        underTest.apply(objectNode, matcher);

        // Then
        assertThat(objectNode.has("testName")).isTrue();
        assertThat(objectNode.has("testName2")).isFalse();
        assertThat(objectNode.has("child")).isFalse();
    }

    @Test
    public void applyShouldFilterAnObjectNodeNestedUnderAnObjectNodeWhenFilterAppliesToChild() {
        // Given
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("testName", "testValue");
        objectNode.put("testName2", "testValue");
        ObjectNode objectNode2 = objectMapper.createObjectNode();
        objectNode2.put("testName", "testValue");
        objectNode2.put("testName2", "testValue");
        objectNode.put("child", objectNode2);

        matcher = Matcher.of("child/testName");

        // When
        underTest.apply(objectNode, matcher);

        // Then
        assertThat(objectNode.has("testName")).isFalse();
        assertThat(objectNode.has("testName2")).isFalse();
        assertThat(objectNode.get("child").has("testName")).isTrue();
        assertThat(objectNode.get("child").has("testName2")).isFalse();
    }

    @Test
    public void applyShouldFilterAnObjectNodeNestedUnderAnObjectNodeWhenFilterAppliesToBothParentAndChild() {
        // Given
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("testName", "testValue");
        objectNode.put("testName2", "testValue");
        ObjectNode objectNode2 = objectMapper.createObjectNode();
        objectNode2.put("testName", "testValue");
        objectNode2.put("testName2", "testValue");
        objectNode.put("child", objectNode2);

        matcher = Matcher.of("testName,child/testName2");

        // When
        underTest.apply(objectNode, matcher);

        // Then
        assertThat(objectNode.has("testName")).isTrue();
        assertThat(objectNode.has("testName2")).isFalse();
        assertThat(objectNode.get("child").has("testName")).isFalse();
        assertThat(objectNode.get("child").has("testName2")).isTrue();
    }
}
