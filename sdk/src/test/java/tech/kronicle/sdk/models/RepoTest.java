package tech.kronicle.sdk.models;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static tech.kronicle.sdk.models.testutils.ComponentStateUtils.createComponentState;
import static tech.kronicle.sdk.utils.ListUtils.unmodifiableUnionOfLists;

public class RepoTest {

    @Test
    public void constructorShouldMakeStatesAnUnmodifiableList() {
        // Given
        Repo underTest = Repo.builder().states(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getStates().add(createComponentState(1)));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void referenceShouldReturnUrl() {
        // Given
        Repo underTest = Repo.builder()
                .url("https://example.com/example.git")
                .build();

        // When
        String returnValue = underTest.reference();

        // Then
        assertThat(returnValue).isEqualTo("https://example.com/example.git");
    }

    @Test
    public void addStatesWhenThereAreNoExistingStatesShouldAddStatesToExistingStates() {
        // Given
        List<ComponentState> newStates = List.of(
                createComponentState(1),
                createComponentState(2)
        );
        Repo underTest = Repo.builder().build();

        // When
        underTest = underTest.addStates(newStates);

        // When
        assertThat(underTest.getStates()).containsExactlyElementsOf(newStates);
    }

    @Test
    public void addStatesWhenThereAreExistingStatesShouldAddStatesToExistingStates() {
        // Given
        List<ComponentState> existingState = List.of(
                createComponentState(3),
                createComponentState(4)
        );
        List<ComponentState> newState = List.of(
                createComponentState(1),
                createComponentState(2)
        );
        Repo underTest = Repo.builder()
                .states(existingState)
                .build();

        // When
        underTest = underTest.addStates(newState);

        // When
        assertThat(underTest.getStates()).containsExactlyElementsOf(
                unmodifiableUnionOfLists(List.of(existingState, newState))
        );
    }
}
