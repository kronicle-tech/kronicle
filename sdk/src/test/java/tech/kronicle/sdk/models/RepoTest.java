package tech.kronicle.sdk.models;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RepoTest {

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
    public void withUpdatedStateShouldPassANewStateObjectToActionWhenStateIsNull() {
        // Given
        ComponentState updatedState = createState(1);
        Repo underTest = Repo.builder().build();
        FakeStateUpdateAction action = new FakeStateUpdateAction(updatedState);

        // When
        Repo returnValue = underTest.withUpdatedState(action::apply);

        // Then
        assertThat(returnValue).isEqualTo(underTest.withState(updatedState));
        assertThat(action.calls).containsExactly(ComponentState.builder().build());
    }

    @Test
    public void withUpdatedStateShouldPassExistingStateObjectToActionWhenStateIsNotNull() {
        // Given
        ComponentState initialState = createState(1);
        ComponentState updatedState = createState(2);
        Repo underTest = Repo.builder()
                .state(initialState)
                .build();
        FakeStateUpdateAction action = new FakeStateUpdateAction(updatedState);

        // When
        Repo returnValue = underTest.withUpdatedState(action::apply);

        // Then
        assertThat(returnValue).isEqualTo(underTest.withState(updatedState));
        assertThat(action.calls).containsExactly(initialState);
    }

    private ComponentState createState(int stateNumber) {
        return ComponentState.builder()
                .environments(List.of(
                        EnvironmentState.builder()
                                .id("test-environment-id-" + stateNumber)
                                .build()
                ))
                .build();
    }

    @RequiredArgsConstructor
    private static class FakeStateUpdateAction {

        private final ComponentState updatedState;
        private final List<ComponentState> calls = new ArrayList<>();

        public ComponentState apply(ComponentState value) {
            calls.add(value);
            return updatedState;
        }
    }
}
