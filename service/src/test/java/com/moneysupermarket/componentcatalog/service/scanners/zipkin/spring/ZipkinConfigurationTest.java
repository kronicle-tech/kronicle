package com.moneysupermarket.componentcatalog.service.scanners.zipkin.spring;

import com.moneysupermarket.componentcatalog.sdk.models.SummaryComponentDependencyNode;
import com.moneysupermarket.componentcatalog.sdk.models.SummarySubComponentDependencyNode;
import com.moneysupermarket.componentcatalog.service.services.MapComparator;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ZipkinConfigurationTest {

    private final MapComparator<String, String> mapComparator = new MapComparator<>();
    private final ZipkinConfiguration underTest = new ZipkinConfiguration();

    @Test
    public void componentNodeComparatorShouldReturnMinusOneWhenComponentId1IsLessThanComponentId2() {
        // Given
        SummaryComponentDependencyNode object1 = SummaryComponentDependencyNode.builder().componentId("a").build();
        SummaryComponentDependencyNode object2 = SummaryComponentDependencyNode.builder().componentId("b").build();

        // When
        int returnValue = underTest.componentNodeComparator().compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(-1);
    }

    @Test
    public void componentNodeComparatorShouldReturnZeroWhenComponentId1IsEqualToComponentId2() {
        // Given
        SummaryComponentDependencyNode object1 = SummaryComponentDependencyNode.builder().componentId("a").build();
        SummaryComponentDependencyNode object2 = SummaryComponentDependencyNode.builder().componentId("a").build();

        // When
        int returnValue = underTest.componentNodeComparator().compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(0);
    }

    @Test
    public void componentNodeComparatorShouldReturnOneWhenComponentId1IsGreaterThanComponentId2() {
        // Given
        SummaryComponentDependencyNode object1 = SummaryComponentDependencyNode.builder().componentId("b").build();
        SummaryComponentDependencyNode object2 = SummaryComponentDependencyNode.builder().componentId("a").build();

        // When
        int returnValue = underTest.componentNodeComparator().compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(1);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnMinusOneWhenComponentId1IsLessThanComponentId2() {
        // Given
        SummarySubComponentDependencyNode object1 = SummarySubComponentDependencyNode.builder().componentId("a").build();
        SummarySubComponentDependencyNode object2 = SummarySubComponentDependencyNode.builder().componentId("b").build();

        // When
        int returnValue = underTest.subComponentNodeComparator(mapComparator).compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(-1);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnZeroWhenComponentId1IsEqualToComponentId2() {
        // Given
        SummarySubComponentDependencyNode object1 = SummarySubComponentDependencyNode.builder().componentId("a").build();
        SummarySubComponentDependencyNode object2 = SummarySubComponentDependencyNode.builder().componentId("a").build();

        // When
        int returnValue = underTest.subComponentNodeComparator(mapComparator).compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(0);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnOneWhenComponentId1IsGreaterThanComponentId2() {
        // Given
        SummarySubComponentDependencyNode object1 = SummarySubComponentDependencyNode.builder().componentId("b").build();
        SummarySubComponentDependencyNode object2 = SummarySubComponentDependencyNode.builder().componentId("a").build();

        // When
        int returnValue = underTest.subComponentNodeComparator(mapComparator).compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(1);
    }

    @Test
    public void subComponentNodeComparatorShouldIgnoreSpanNamesWhenComponentIdsAreDifferent() {
        // Given
        SummarySubComponentDependencyNode object1 = SummarySubComponentDependencyNode.builder().componentId("a").spanName("b").build();
        SummarySubComponentDependencyNode object2 = SummarySubComponentDependencyNode.builder().componentId("b").spanName("a").build();

        // When
        int returnValue = underTest.subComponentNodeComparator(mapComparator).compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(-1);
    }

    @Test
    public void subComponentNodeComparatorShouldIgnoreTagsWhenComponentIdsAreDifferent() {
        // Given
        SummarySubComponentDependencyNode object1 = SummarySubComponentDependencyNode.builder()
                .componentId("a")
                .tags(Map.ofEntries(Map.entry("b", "b")))
                .build();
        SummarySubComponentDependencyNode object2 = SummarySubComponentDependencyNode.builder()
                .componentId("b")
                .tags(Map.ofEntries(Map.entry("a", "a")))
                .build();

        // When
        int returnValue = underTest.subComponentNodeComparator(mapComparator).compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(-1);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnMinusOneWhenSpanName1IsLessThanSpanName2() {
        // Given
        SummarySubComponentDependencyNode object1 = SummarySubComponentDependencyNode.builder().componentId("a").spanName("a").build();
        SummarySubComponentDependencyNode object2 = SummarySubComponentDependencyNode.builder().componentId("a").spanName("b").build();

        // When
        int returnValue = underTest.subComponentNodeComparator(mapComparator).compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(-1);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnZeroWhenSpanName1IsEqualToSpanName2() {
        // Given
        SummarySubComponentDependencyNode object1 = SummarySubComponentDependencyNode.builder().componentId("a").spanName("a").build();
        SummarySubComponentDependencyNode object2 = SummarySubComponentDependencyNode.builder().componentId("a").spanName("a").build();

        // When
        int returnValue = underTest.subComponentNodeComparator(mapComparator).compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(0);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnOneWhenSpanName1IsGreaterThanSpanName2() {
        // Given
        SummarySubComponentDependencyNode object1 = SummarySubComponentDependencyNode.builder().componentId("a").spanName("b").build();
        SummarySubComponentDependencyNode object2 = SummarySubComponentDependencyNode.builder().componentId("a").spanName("a").build();

        // When
        int returnValue = underTest.subComponentNodeComparator(mapComparator).compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(1);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnMinusOneWhenSpanName1IsNullAndSpanName2IsNotNull() {
        // Given
        SummarySubComponentDependencyNode object1 = SummarySubComponentDependencyNode.builder().componentId("a").spanName(null).build();
        SummarySubComponentDependencyNode object2 = SummarySubComponentDependencyNode.builder().componentId("a").spanName("a").build();

        // When
        int returnValue = underTest.subComponentNodeComparator(mapComparator).compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(-1);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnZeroWhenSpanName1AndSpanName2AreBothNull() {
        // Given
        SummarySubComponentDependencyNode object1 = SummarySubComponentDependencyNode.builder().componentId("a").spanName(null).build();
        SummarySubComponentDependencyNode object2 = SummarySubComponentDependencyNode.builder().componentId("a").spanName(null).build();

        // When
        int returnValue = underTest.subComponentNodeComparator(mapComparator).compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(0);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnOneWhenSpanName1IsNotNullAndSpanName2IsNull() {
        // Given
        SummarySubComponentDependencyNode object1 = SummarySubComponentDependencyNode.builder().componentId("a").spanName("a").build();
        SummarySubComponentDependencyNode object2 = SummarySubComponentDependencyNode.builder().componentId("a").spanName(null).build();

        // When
        int returnValue = underTest.subComponentNodeComparator(mapComparator).compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(1);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnMinusOneWhenTags1IsLessThanTags2() {
        // Given
        SummarySubComponentDependencyNode object1 = SummarySubComponentDependencyNode.builder()
                .componentId("a")
                .spanName("a")
                .tags(Map.ofEntries(Map.entry("a", "a")))
                .build();
        SummarySubComponentDependencyNode object2 = SummarySubComponentDependencyNode.builder()
                .componentId("a")
                .spanName("a")
                .tags(Map.ofEntries(Map.entry("b", "b")))
                .build();

        // When
        int returnValue = underTest.subComponentNodeComparator(mapComparator).compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(-1);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnZeroWhenTags1IsEqualToTags2() {
        // Given
        SummarySubComponentDependencyNode object1 = SummarySubComponentDependencyNode.builder()
                .componentId("a")
                .spanName("a")
                .tags(Map.ofEntries(Map.entry("a", "a")))
                .build();
        SummarySubComponentDependencyNode object2 = SummarySubComponentDependencyNode.builder()
                .componentId("a")
                .spanName("a")
                .tags(Map.ofEntries(Map.entry("a", "a")))
                .build();

        // When
        int returnValue = underTest.subComponentNodeComparator(mapComparator).compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(0);
    }

    @Test
    public void subComponentNodeComparatorShouldReturnOneWhenTags1IsGreaterThanTags2() {
        // Given
        SummarySubComponentDependencyNode object1 = SummarySubComponentDependencyNode.builder()
                .componentId("a")
                .spanName("a")
                .tags(Map.ofEntries(Map.entry("b", "b")))
                .build();
        SummarySubComponentDependencyNode object2 = SummarySubComponentDependencyNode.builder()
                .componentId("a")
                .spanName("a")
                .tags(Map.ofEntries(Map.entry("a", "a")))
                .build();

        // When
        int returnValue = underTest.subComponentNodeComparator(mapComparator).compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(1);
    }

    @Test
    public void subComponentNodeComparatorShouldIgnoreTagsWhenSpanNamesAreDifferent() {
        // Given
        SummarySubComponentDependencyNode object1 = SummarySubComponentDependencyNode.builder()
                .componentId("a")
                .spanName("a")
                .tags(Map.ofEntries(Map.entry("b", "b")))
                .build();
        SummarySubComponentDependencyNode object2 = SummarySubComponentDependencyNode.builder()
                .componentId("a")
                .spanName("b")
                .tags(Map.ofEntries(Map.entry("a", "a")))
                .build();

        // When
        int returnValue = underTest.subComponentNodeComparator(mapComparator).compare(object1, object2);

        // Then
        assertThat(returnValue).isEqualTo(-1);
    }
}