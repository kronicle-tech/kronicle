//package tech.kronicle.service.scanners.gradle.internal.utils;
//
//import org.assertj.core.util.Lists;
//import org.junit.jupiter.api.Test;
//
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//import java.util.Spliterator;
//import java.util.stream.Collectors;
//import java.util.stream.StreamSupport;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//public class InheritingHashSetTest {
//
//    @Test
//    public void sizeShouldReturnSizeWhenNoParent() {
//        // Given
//        InheritingHashSet<String> underTest = new InheritingHashSet<>();
//        underTest.add("value-1");
//        underTest.add("value-2");
//
//        // When
//        int returnValue = underTest.size();
//
//        // Then
//        assertThat(returnValue).isEqualTo(2);
//    }
//
//    @Test
//    public void sizeShouldReturnCombinedSizeOfThisSetAndParent() {
//        // Given
//        Set<String> parent = new HashSet<>();
//        parent.add("value-1");
//        parent.add("value-2");
//        InheritingHashSet<String> underTest = new InheritingHashSet<>(parent);
//        underTest.add("value-3");
//        underTest.add("value-4");
//
//        // When
//        int returnValue = underTest.size();
//
//        // Then
//        assertThat(parent.size()).isEqualTo(2);
//        assertThat(returnValue).isEqualTo(4);
//    }
//
//    @Test
//    public void sizeShouldIgnoreDuplicatesBetweenThisSetAndParent() {
//        // Given
//        Set<String> parent = new HashSet<>();
//        parent.add("value-1");
//        parent.add("value-2");
//        InheritingHashSet<String> underTest = new InheritingHashSet<>(parent);
//        underTest.add("value-2");
//        underTest.add("value-3");
//
//        // When
//        int returnValue = underTest.size();
//
//        // Then
//        assertThat(parent.size()).isEqualTo(2);
//        assertThat(returnValue).isEqualTo(3);
//    }
//
//    @Test
//    public void isEmptyShouldBeTrueWhenThisSetIsEmptyAndNoParent() {
//        // Given
//        InheritingHashSet<String> underTest = new InheritingHashSet<>();
//
//        // When
//        boolean returnValue = underTest.isEmpty();
//
//        // Then
//        assertThat(returnValue).isTrue();
//    }
//
//    @Test
//    public void isEmptyShouldBeFalseWhenThisSetIsNotEmptyAndNoParent() {
//        // Given
//        InheritingHashSet<String> underTest = new InheritingHashSet<>();
//        underTest.add("value-1");
//
//        // When
//        boolean returnValue = underTest.isEmpty();
//
//        // Then
//        assertThat(returnValue).isFalse();
//    }
//
//    @Test
//    public void isEmptyShouldBeTrueWhenThisSetIsEmptyAndParentIsEmpty() {
//        // Given
//        Set<String> parent = new HashSet<>();
//        InheritingHashSet<String> underTest = new InheritingHashSet<>(parent);
//
//        // When
//        boolean returnValue = underTest.isEmpty();
//
//        // Then
//        assertThat(returnValue).isTrue();
//    }
//
//    @Test
//    public void isEmptyShouldBeFalseWhenThisSetIsNotEmptyAndParentIsEmpty() {
//        // Given
//        Set<String> parent = new HashSet<>();
//        InheritingHashSet<String> underTest = new InheritingHashSet<>(parent);
//        underTest.add("value-1");
//
//        // When
//        boolean returnValue = underTest.isEmpty();
//
//        // Then
//        assertThat(returnValue).isFalse();
//    }
//
//    @Test
//    public void isEmptyShouldBeFalseWhenThisSetIsEmptyAndParentIsNotEmpty() {
//        // Given
//        Set<String> parent = new HashSet<>();
//        InheritingHashSet<String> underTest = new InheritingHashSet<>(parent);
//        underTest.add("value-1");
//
//        // When
//        boolean returnValue = underTest.isEmpty();
//
//        // Then
//        assertThat(returnValue).isFalse();
//    }
//
//    @Test
//    public void iteratorShouldIterateThisSetWhenNoParent() {
//        // Given
//        InheritingHashSet<String> underTest = new InheritingHashSet<>();
//        underTest.add("value-1");
//        underTest.add("value-2");
//
//        // When
//        Iterator<String> returnValue = underTest.iterator();
//
//        // Then
//        List<String> returnValueList = Lists.newArrayList(returnValue);
//        assertThat(returnValueList).containsExactlyInAnyOrder("value-1", "value-2");
//    }
//
//    @Test
//    public void iteratorShouldBeEmptyWhenThisSetIsEmptyAndNoParent() {
//        // Given
//        InheritingHashSet<String> underTest = new InheritingHashSet<>();
//
//        // When
//        Iterator<String> returnValue = underTest.iterator();
//
//        // Then
//        List<String> returnValueList = Lists.newArrayList(returnValue);
//        assertThat(returnValueList).isEmpty();
//    }
//
//    @Test
//    public void iteratorShouldIterateThisSetAndParent() {
//        // Given
//        Set<String> parent = new HashSet<>();
//        parent.add("value-1");
//        parent.add("value-2");
//        InheritingHashSet<String> underTest = new InheritingHashSet<>(parent);
//        underTest.add("value-3");
//        underTest.add("value-4");
//
//        // When
//        Iterator<String> returnValue = underTest.iterator();
//
//        // Then
//        List<String> returnValueList = Lists.newArrayList(returnValue);
//        assertThat(returnValueList).containsExactlyInAnyOrder("value-1", "value-2", "value-3", "value-4");
//    }
//
//    @Test
//    public void iteratorShouldIgnoreDuplicatesBetweenThisSetAndParent() {
//        // Given
//        Set<String> parent = new HashSet<>();
//        parent.add("value-1");
//        parent.add("value-2");
//        InheritingHashSet<String> underTest = new InheritingHashSet<>(parent);
//        underTest.add("value-2");
//        underTest.add("value-3");
//
//        // When
//        Iterator<String> returnValue = underTest.iterator();
//
//        // Then
//        List<String> returnValueList = Lists.newArrayList(returnValue);
//        assertThat(returnValueList).containsExactlyInAnyOrder("value-1", "value-2", "value-3");
//    }
//
//    @Test
//    public void iteratorShouldIterateThisSetOnlyWhenParentIsEmpty() {
//        // Given
//        Set<String> parent = new HashSet<>();
//        InheritingHashSet<String> underTest = new InheritingHashSet<>(parent);
//        underTest.add("value-1");
//        underTest.add("value-2");
//
//        // When
//        Iterator<String> returnValue = underTest.iterator();
//
//        // Then
//        List<String> returnValueList = Lists.newArrayList(returnValue);
//        assertThat(returnValueList).containsExactlyInAnyOrder("value-1", "value-2");
//    }
//
//    @Test
//    public void iteratorShouldIterateParentOnlyWhenThisSetIsEmpty() {
//        // Given
//        Set<String> parent = new HashSet<>();
//        parent.add("value-1");
//        parent.add("value-2");
//        InheritingHashSet<String> underTest = new InheritingHashSet<>(parent);
//
//        // When
//        Iterator<String> returnValue = underTest.iterator();
//
//        // Then
//        List<String> returnValueList = Lists.newArrayList(returnValue);
//        assertThat(returnValueList).containsExactlyInAnyOrder("value-1", "value-2");
//    }
//
//    @Test
//    public void iteratorShouldBeEmptyWhenThisSetIsEmptyAndParentIsEmpty() {
//        // Given
//        Set<String> parent = new HashSet<>();
//        InheritingHashSet<String> underTest = new InheritingHashSet<>(parent);
//
//        // When
//        Iterator<String> returnValue = underTest.iterator();
//
//        // Then
//        List<String> returnValueList = Lists.newArrayList(returnValue);
//        assertThat(returnValueList).isEmpty();
//    }
//
//    @Test
//    public void spliteratorShouldIterateThisSetWhenNoParent() {
//        // Given
//        InheritingHashSet<String> underTest = new InheritingHashSet<>();
//        underTest.add("value-1");
//        underTest.add("value-2");
//
//        // When
//        Spliterator<String> returnValue = underTest.spliterator();
//
//        // Then
//        List<String> returnValueList = StreamSupport.stream(returnValue, false).collect(Collectors.toList());
//        assertThat(returnValueList).containsExactlyInAnyOrder("value-1", "value-2");
//    }
//
//    @Test
//    public void spliteratorShouldBeEmptyWhenThisSetIsEmptyAndNoParent() {
//        // Given
//        InheritingHashSet<String> underTest = new InheritingHashSet<>();
//
//        // When
//        Spliterator<String> returnValue = underTest.spliterator();
//
//        // Then
//        List<String> returnValueList = StreamSupport.stream(returnValue, false).collect(Collectors.toList());
//        assertThat(returnValueList).isEmpty();
//    }
//
//    @Test
//    public void spliteratorShouldIterateThisSetAndParent() {
//        // Given
//        Set<String> parent = new HashSet<>();
//        parent.add("value-1");
//        parent.add("value-2");
//        InheritingHashSet<String> underTest = new InheritingHashSet<>(parent);
//        underTest.add("value-3");
//        underTest.add("value-4");
//
//        // When
//        Spliterator<String> returnValue = underTest.spliterator();
//
//        // Then
//        List<String> returnValueList = StreamSupport.stream(returnValue, false).collect(Collectors.toList());
//        assertThat(returnValueList).containsExactlyInAnyOrder("value-1", "value-2", "value-3", "value-4");
//    }
//
//    @Test
//    public void spliteratorShouldIgnoreDuplicatesBetweenThisSetAndParent() {
//        // Given
//        Set<String> parent = new HashSet<>();
//        parent.add("value-1");
//        parent.add("value-2");
//        InheritingHashSet<String> underTest = new InheritingHashSet<>(parent);
//        underTest.add("value-2");
//        underTest.add("value-3");
//
//        // When
//        Spliterator<String> returnValue = underTest.spliterator();
//
//        // Then
//        List<String> returnValueList = StreamSupport.stream(returnValue, false).collect(Collectors.toList());
//        assertThat(returnValueList).containsExactlyInAnyOrder("value-1", "value-2", "value-3");
//    }
//
//    @Test
//    public void spliteratorShouldIterateThisSetOnlyWhenParentIsEmpty() {
//        // Given
//        Set<String> parent = new HashSet<>();
//        InheritingHashSet<String> underTest = new InheritingHashSet<>(parent);
//        underTest.add("value-1");
//        underTest.add("value-2");
//
//        // When
//        Spliterator<String> returnValue = underTest.spliterator();
//
//        // Then
//        List<String> returnValueList = StreamSupport.stream(returnValue, false).collect(Collectors.toList());
//        assertThat(returnValueList).containsExactlyInAnyOrder("value-1", "value-2");
//    }
//
//    @Test
//    public void spliteratorShouldIterateParentOnlyWhenThisSetIsEmpty() {
//        // Given
//        Set<String> parent = new HashSet<>();
//        parent.add("value-1");
//        parent.add("value-2");
//        InheritingHashSet<String> underTest = new InheritingHashSet<>(parent);
//
//        // When
//        Spliterator<String> returnValue = underTest.spliterator();
//
//        // Then
//        List<String> returnValueList = StreamSupport.stream(returnValue, false).collect(Collectors.toList());
//        assertThat(returnValueList).containsExactlyInAnyOrder("value-1", "value-2");
//    }
//
//    @Test
//    public void spliteratorShouldBeEmptyWhenThisSetIsEmptyAndParentIsEmpty() {
//        // Given
//        Set<String> parent = new HashSet<>();
//        InheritingHashSet<String> underTest = new InheritingHashSet<>(parent);
//
//        // When
//        Spliterator<String> returnValue = underTest.spliterator();
//
//        // Then
//        List<String> returnValueList = StreamSupport.stream(returnValue, false).collect(Collectors.toList());
//        assertThat(returnValueList).isEmpty();
//    }
//}
