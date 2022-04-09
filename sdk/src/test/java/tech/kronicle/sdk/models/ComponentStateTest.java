package tech.kronicle.sdk.models;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ComponentStateTest {

    @Test
    public void constructorShouldMakeEnvironmentsAnUnmodifiableList() {
        // Given
        ComponentState underTest = ComponentState.builder().environments(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getEnvironments().add(
                ComponentStateEnvironment.builder().build())
        );

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void withUpdatedEnvironmentShouldPassANewEnvironmentObjectToActionWhenEnvironmentDoesNotExist() {
        // Given
        ComponentStateEnvironment updatedEnvironment = createEnvironment(1, 1);
        ComponentState underTest = ComponentState.builder().build();
        FakeEnvironmentUpdateAction action = new FakeEnvironmentUpdateAction(updatedEnvironment);

        // When
        ComponentState returnValue = underTest.withUpdatedEnvironment(createEnvironmentId(1), action::apply);

        // Then
        assertThat(returnValue).isEqualTo(underTest.withEnvironments(List.of(updatedEnvironment)));
        assertThat(action.calls).containsExactly(
                ComponentStateEnvironment.builder()
                        .id(createEnvironmentId(1))
                        .build()
        );
    }

    @Test
    public void withUpdatedEnvironmentShouldKeepExistingEnvironmentsWhenAddingANewOne() {
        // Given
        ComponentStateEnvironment updatedEnvironment = createEnvironment(1, 1);
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
                ComponentStateEnvironment.builder()
                        .id(createEnvironmentId(1))
                        .build()
        );
    }

    @Test
    public void withUpdatedEnvironmentShouldPassExistingEnvironmentObjectToActionWhenEnvironmentAlreadyExists() {
        // Given
        ComponentStateEnvironment initialEnvironment = createEnvironment(1, 1);
        ComponentStateEnvironment updatedEnvironment = createEnvironment(1, 2);
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
        ComponentStateEnvironment initialEnvironment = createEnvironment(1, 1);
        ComponentStateEnvironment updatedEnvironment = createEnvironment(1, 2);
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

    private ComponentStateEnvironment createEnvironment(int environmentNumber, int pluginNumber) {
        return ComponentStateEnvironment.builder()
                .id(createEnvironmentId(environmentNumber))
                .plugins(List.of(
                        ComponentStateEnvironmentPlugin.builder()
                                .id("test-plugin-id-" + environmentNumber + "-" + pluginNumber)
                                .build()
                ))
                .build();
    }

    private String createEnvironmentId(int environmentNumber) {
        return "test-environment-id-" + environmentNumber;
    }

    @RequiredArgsConstructor
    private static class FakeEnvironmentUpdateAction {

        private final ComponentStateEnvironment updatedEnvironment;
        private final List<ComponentStateEnvironment> calls = new ArrayList<>();

        public ComponentStateEnvironment apply(ComponentStateEnvironment value) {
            calls.add(value);
            return updatedEnvironment;
        }
    }
}
