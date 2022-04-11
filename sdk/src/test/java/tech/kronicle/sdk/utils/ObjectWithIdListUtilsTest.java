package tech.kronicle.sdk.utils;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.ObjectWithIdAndMerge;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.sdk.utils.ListUtils.unmodifiableUnionOfLists;

public class ObjectWithIdListUtilsTest {

    @Test
    public void mergeObjectWithIdListsShouldMergeEmptyLists() {
        // When
        List<TestItem> returnValue = ObjectWithIdListUtils.<TestItem>mergeObjectWithIdLists(List.of(), List.of());

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void mergeObjectWithIdListsShouldMergeItemsWithSameId() {
        // When
        List<TestItem> returnValue = ObjectWithIdListUtils.<TestItem>mergeObjectWithIdLists(
                List.of(new TestItem("id-1", List.of("value-a"))),
                List.of(new TestItem("id-1", List.of("value-b")))
        );

        // Then
        assertThat(returnValue).containsExactly(
                new TestItem("id-1", List.of("value-a", "value-b"))
        );
    }

    @Test
    public void mergeObjectWithIdListsShouldNotMergeItemsWithDifferentIds() {
        // When
        List<TestItem> returnValue = ObjectWithIdListUtils.<TestItem>mergeObjectWithIdLists(
                List.of(new TestItem("id-1", List.of("value-a"))),
                List.of(new TestItem("id-2", List.of("value-b")))
        );

        // Then
        assertThat(returnValue).containsExactly(
                new TestItem("id-1", List.of("value-a")),
                new TestItem("id-2", List.of("value-b"))
        );
    }

    @Test
    public void mergeObjectWithIdListsShouldPlaceAllItemsFromListAAheadOfAllItemsFromListB() {
        // When
        List<TestItem> returnValue = ObjectWithIdListUtils.<TestItem>mergeObjectWithIdLists(
                List.of(
                        new TestItem("id-1", List.of("value-a")),
                        new TestItem("id-3", List.of("value-c")),
                        new TestItem("id-5", List.of("value-e")),
                        new TestItem("id-7", List.of("value-g")),
                        new TestItem("id-9", List.of("value-i"))
                ),
                List.of(
                        new TestItem("id-2", List.of("value-b")),
                        new TestItem("id-4", List.of("value-d")),
                        new TestItem("id-6", List.of("value-f")),
                        new TestItem("id-8", List.of("value-h")),
                        new TestItem("id-10", List.of("value-j"))
                )
        );

        // Then
        assertThat(returnValue).containsExactly(
                new TestItem("id-1", List.of("value-a")),
                new TestItem("id-3", List.of("value-c")),
                new TestItem("id-5", List.of("value-e")),
                new TestItem("id-7", List.of("value-g")),
                new TestItem("id-9", List.of("value-i")),
                new TestItem("id-2", List.of("value-b")),
                new TestItem("id-4", List.of("value-d")),
                new TestItem("id-6", List.of("value-f")),
                new TestItem("id-8", List.of("value-h")),
                new TestItem("id-10", List.of("value-j"))
        );
    }

    @Value
    private static class TestItem implements ObjectWithIdAndMerge<TestItem> {

        String id;
        List<String> values;

        @Override
        public TestItem merge(TestItem value) {
            return new TestItem(this.id, unmodifiableUnionOfLists(List.of(this.values, value.values)));
        }
    }
}
