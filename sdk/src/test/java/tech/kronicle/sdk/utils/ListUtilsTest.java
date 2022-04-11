package tech.kronicle.sdk.utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ListUtilsTest {

    @Test
    public void createUnmodifiableListShouldConvertListToAnUnmodifiableList() {
        // Given
        List<String> modifiableList = createModifiableList();

        // When
        List<String> returnValue = ListUtils.createUnmodifiableList(modifiableList);
        Throwable thrown = catchThrowable(() -> returnValue.add("new-item"));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void createUnmodifiableListShouldConvertListToAnUnmodifiableListThatDoesNotReflectChangesToOriginalList() {
        // Given
        List<String> modifiableList = createModifiableList();

        // When
        List<String> returnValue = ListUtils.createUnmodifiableList(modifiableList);
        modifiableList.add("new-item");

        // Then
        assertThat(returnValue).containsExactly("test-1", "test-2");
    }

    @Test
    public void unmodifiableUnionOfListsShouldConcatenateListsToAnUnmodifiableList() {
        // Given
        List<String> modifiableList1 = createModifiableList("list-1-");
        List<String> modifiableList2 = createModifiableList("list-2-");

        // When
        List<String> returnValue = ListUtils.unmodifiableUnionOfLists(List.of(
                modifiableList1, 
                modifiableList2
        ));
        Throwable thrown = catchThrowable(() -> returnValue.add("new-item"));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void unmodifiableUnionOfListsShouldCreateAnUnmodifiableListThatDoesNotReflectChangesToOriginalLists() {
        // Given
        List<String> modifiableList1 = createModifiableList("list-1-");
        List<String> modifiableList2 = createModifiableList("list-2-");

        // When
        List<String> returnValue = ListUtils.unmodifiableUnionOfLists(List.of(
                modifiableList1,
                modifiableList2
        ));
        modifiableList1.add("new-item");
        modifiableList2.add("new-item");

        // Then
        assertThat(returnValue).containsExactly("list-1-test-1", "list-1-test-2", "list-2-test-1", "list-2-test-2");
    }

    private ArrayList<String> createModifiableList() {
        return createModifiableList("");
    }

    private ArrayList<String> createModifiableList(String itemPrefix) {
        ArrayList<String> modifiableList = new ArrayList<>();
        modifiableList.add(itemPrefix + "test-1");
        modifiableList.add(itemPrefix + "test-2");
        return modifiableList;
    }
}
