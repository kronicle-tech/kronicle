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
        Throwable thrown = catchThrowable(() -> returnValue.add("test-3"));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void createUnmodifiableListShouldConvertListToAnUnmodifiableListThatDoesNotReflectChangesToOriginalList() {
        // Given
        List<String> modifiableList = createModifiableList();

        // When
        List<String> returnValue = ListUtils.createUnmodifiableList(modifiableList);
        modifiableList.add("test-3");

        // Then
        assertThat(returnValue).containsExactly("test-1", "test-2");
    }

    private ArrayList<String> createModifiableList() {
        ArrayList<String> modifiableList = new ArrayList<>();
        modifiableList.add("test-1");
        modifiableList.add("test-2");
        return modifiableList;
    }
}
