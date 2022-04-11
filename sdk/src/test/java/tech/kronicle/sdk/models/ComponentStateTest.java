package tech.kronicle.sdk.models;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ComponentStateTest {

    @Test
    public void constructorShouldMakeEnvironmentsAnUnmodifiableList() {
        // Given
        ComponentState underTest = ComponentState.builder().environments(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getEnvironments().add(
                EnvironmentState.builder().build())
        );

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void withUpdatedEnvironmentShouldPassANewEnvironmentObjectToActionWhenEnvironmentDoesNotExist() {
        // Given
        EnvironmentState updatedEnvironment = createEnvironment(1, 1);
        ComponentState underTest = ComponentState.builder().build();
        FakeEnvironmentUpdateAction action = new FakeEnvironmentUpdateAction(updatedEnvironment);

        // When
        ComponentState returnValue = underTest.withUpdatedEnvironment(createEnvironmentId(1), action::apply);

        // Then
        assertThat(returnValue).isEqualTo(underTest.withEnvironments(List.of(updatedEnvironment)));
        assertThat(action.calls).containsExactly(
                EnvironmentState.builder()
                        .id(createEnvironmentId(1))
                        .build()
        );
    }

    @Test
    public void withUpdatedEnvironmentShouldKeepExistingEnvironmentsWhenAddingANewOne() {
        // Given
        EnvironmentState updatedEnvironment = createEnvironment(1, 1);
        ComponentState underTest = ComponentState.builder()
                .environments(List.of(
                        createEnvironment(2, 1),
                        createEnvironment(3, 1)
                ))
                .build();
        FakeEnvironmentUpdateAction action = new FakeEnvironmentUpdateAction(updatedEnvironment);

        // When
        ComponentState returnValue = underTest.withUpdatedEnvironment(createEnvironmentId(1), action::apply);

        // Then
        assertThat(returnValue).isEqualTo(underTest.withEnvironments(List.of(
                createEnvironment(2, 1),
                createEnvironment(3, 1),
                updatedEnvironment
        )));
        assertThat(action.calls).containsExactly(
                EnvironmentState.builder()
                        .id(createEnvironmentId(1))
                        .build()
        );
    }

    @Test
    public void withUpdatedEnvironmentShouldPassExistingEnvironmentObjectToActionWhenEnvironmentAlreadyExists() {
        // Given
        EnvironmentState initialEnvironment = createEnvironment(1, 1);
        EnvironmentState updatedEnvironment = createEnvironment(1, 2);
        ComponentState underTest = ComponentState.builder()
                .environments(List.of(initialEnvironment))
                .build();
        FakeEnvironmentUpdateAction action = new FakeEnvironmentUpdateAction(updatedEnvironment);

        // When
        ComponentState returnValue = underTest.withUpdatedEnvironment(createEnvironmentId(1), action::apply);

        // Then
        assertThat(returnValue).isEqualTo(underTest.withEnvironments(List.of(updatedEnvironment)));
        assertThat(action.calls).containsExactly(initialEnvironment);
    }

    @Test
    public void withUpdatedEnvironmentShouldKeepExistingEnvironmentsWhenUpdatingOne() {
        // Given
        EnvironmentState initialEnvironment = createEnvironment(1, 1);
        EnvironmentState updatedEnvironment = createEnvironment(1, 2);
        ComponentState underTest = ComponentState.builder()
                .environments(List.of(
                        initialEnvironment,
                        createEnvironment(2, 1),
                        createEnvironment(3, 1)
                ))
                .build();
        FakeEnvironmentUpdateAction action = new FakeEnvironmentUpdateAction(updatedEnvironment);

        // When
        ComponentState returnValue = underTest.withUpdatedEnvironment(createEnvironmentId(1), action::apply);

        // Then
        assertThat(returnValue).isEqualTo(underTest.withEnvironments(List.of(
                updatedEnvironment,
                createEnvironment(2, 1),
                createEnvironment(3, 1)
        )));
        assertThat(action.calls).containsExactly(initialEnvironment);
    }

    @Test
    public void mergeShouldMergeEnvironments() {
        // Given
        EnvironmentState environment1 = createEnvironment(1, 1);
        EnvironmentState environment2A = createEnvironment(2, 1);
        EnvironmentState environment2B = createEnvironment(2, 2);
        EnvironmentState environment3 = createEnvironment(3, 1);
        ComponentState underTest1 = ComponentState.builder()
                .environments(List.of(
                        environment1,
                        environment2A
                ))
                .build();
        ComponentState underTest2 = ComponentState.builder()
                .environments(List.of(
                        environment2B,
                        environment3
                ))
                .build();

        // When
        ComponentState returnValue = underTest1.merge(underTest2);

        // Then
        assertThat(returnValue.getEnvironments()).containsExactly(
                environment1,
                createEnvironment(2, List.of(1, 2)),
                environment3
        );
    }

    private EnvironmentState createEnvironment(int environmentNumber, int pluginNumber) {
        return createEnvironment(environmentNumber, List.of(pluginNumber));
    }

    private EnvironmentState createEnvironment(int environmentNumber, List<Integer> pluginNumbers) {
        return EnvironmentState.builder()
                .id(createEnvironmentId(environmentNumber))
                .plugins(
                        pluginNumbers.stream()
                                .map(pluginNumber -> createPlugin(environmentNumber, pluginNumber))
                                .collect(toUnmodifiableList())
                )
                .build();
    }

    private EnvironmentPluginState createPlugin(int environmentNumber, int pluginNumber) {
        return EnvironmentPluginState.builder()
                .id("test-plugin-id-" + environmentNumber + "-" + pluginNumber)
                .build();
    }

    private String createEnvironmentId(int environmentNumber) {
        return "test-environment-id-" + environmentNumber;
    }

    @RequiredArgsConstructor
    private static class FakeEnvironmentUpdateAction {

        private final EnvironmentState updatedEnvironment;
        private final List<EnvironmentState> calls = new ArrayList<>();

        public EnvironmentState apply(EnvironmentState value) {
            calls.add(value);
            return updatedEnvironment;
        }
    }
}
