package tech.kronicle.sdk.models;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class EnvironmentStateTest {

    @Test
    public void constructorShouldMakeEnvironmentPluginsAnUnmodifiableList() {
        // Given
        EnvironmentState underTest = EnvironmentState.builder().plugins(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getPlugins().add(
                EnvironmentPluginState.builder().build())
        );

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void withUpdatedPluginShouldPassANewEnvironmentPluginObjectToActionWhenEnvironmentPluginDoesNotExist() {
        // Given
        EnvironmentPluginState updatedEnvironmentPlugin = createEnvironmentPlugin(1, 1);
        EnvironmentState underTest = EnvironmentState.builder().build();
        FakeEnvironmentPluginUpdateAction action = new FakeEnvironmentPluginUpdateAction(updatedEnvironmentPlugin);

        // When
        EnvironmentState returnValue = underTest.withUpdatedPlugin(createPluginId(1), action::apply);

        // Then
        assertThat(returnValue).isEqualTo(underTest.withPlugins(List.of(updatedEnvironmentPlugin)));
        assertThat(action.calls).containsExactly(
                EnvironmentPluginState.builder()
                        .id(createPluginId(1))
                        .build()
        );
    }

    @Test
    public void withUpdatedPluginShouldKeepExistingEnvironmentPluginsWhenAddingANewOne() {
        // Given
        EnvironmentPluginState updatedEnvironmentPlugin = createEnvironmentPlugin(1, 1);
        EnvironmentState underTest = EnvironmentState.builder()
                .plugins(List.of(
                        createEnvironmentPlugin(2, 1),
                        createEnvironmentPlugin(3, 1)
                ))
                .build();
        FakeEnvironmentPluginUpdateAction action = new FakeEnvironmentPluginUpdateAction(updatedEnvironmentPlugin);

        // When
        EnvironmentState returnValue = underTest.withUpdatedPlugin(createPluginId(1), action::apply);

        // Then
        assertThat(returnValue).isEqualTo(underTest.withPlugins(List.of(
                createEnvironmentPlugin(2, 1),
                createEnvironmentPlugin(3, 1),
                updatedEnvironmentPlugin
        )));
        assertThat(action.calls).containsExactly(
                EnvironmentPluginState.builder()
                        .id(createPluginId(1))
                        .build()
        );
    }

    @Test
    public void withUpdatedPluginShouldPassExistingEnvironmentPluginObjectToActionWhenEnvironmentPluginAlreadyExists() {
        // Given
        EnvironmentPluginState initialEnvironmentPlugin = createEnvironmentPlugin(1, 1);
        EnvironmentPluginState updatedEnvironmentPlugin = createEnvironmentPlugin(1, 2);
        EnvironmentState underTest = EnvironmentState.builder()
                .plugins(List.of(initialEnvironmentPlugin))
                .build();
        FakeEnvironmentPluginUpdateAction action = new FakeEnvironmentPluginUpdateAction(updatedEnvironmentPlugin);

        // When
        EnvironmentState returnValue = underTest.withUpdatedPlugin(createPluginId(1), action::apply);

        // Then
        assertThat(returnValue).isEqualTo(underTest.withPlugins(List.of(updatedEnvironmentPlugin)));
        assertThat(action.calls).containsExactly(initialEnvironmentPlugin);
    }

    @Test
    public void withUpdatedPluginShouldKeepExistingEnvironmentPluginsWhenUpdatingOne() {
        // Given
        EnvironmentPluginState initialEnvironmentPlugin = createEnvironmentPlugin(1, 1);
        EnvironmentPluginState updatedEnvironmentPlugin = createEnvironmentPlugin(1, 2);
        EnvironmentState underTest = EnvironmentState.builder()
                .plugins(List.of(
                        initialEnvironmentPlugin,
                        createEnvironmentPlugin(2, 1),
                        createEnvironmentPlugin(3, 1)
                ))
                .build();
        FakeEnvironmentPluginUpdateAction action = new FakeEnvironmentPluginUpdateAction(updatedEnvironmentPlugin);

        // When
        EnvironmentState returnValue = underTest.withUpdatedPlugin(createPluginId(1), action::apply);

        // Then
        assertThat(returnValue).isEqualTo(underTest.withPlugins(List.of(
                updatedEnvironmentPlugin,
                createEnvironmentPlugin(2, 1),
                createEnvironmentPlugin(3, 1)
        )));
        assertThat(action.calls).containsExactly(initialEnvironmentPlugin);
    }

    @Test
    public void mergeShouldMergeEnvironmentPlugins() {
        // Given
        EnvironmentPluginState environmentPlugin1 = createEnvironmentPlugin(1, 1);
        EnvironmentPluginState environmentPlugin2A = createEnvironmentPlugin(2, 1);
        EnvironmentPluginState environmentPlugin2B = createEnvironmentPlugin(2, 2);
        EnvironmentPluginState environmentPlugin3 = createEnvironmentPlugin(3, 1);
        EnvironmentState underTest1 = EnvironmentState.builder()
                .plugins(List.of(
                        environmentPlugin1,
                        environmentPlugin2A
                ))
                .build();
        EnvironmentState underTest2 = EnvironmentState.builder()
                .plugins(List.of(
                        environmentPlugin2B,
                        environmentPlugin3
                ))
                .build();

        // When
        EnvironmentState returnValue = underTest1.merge(underTest2);

        // Then
        assertThat(returnValue.getPlugins()).containsExactly(
                environmentPlugin1,
                createEnvironmentPlugin(2, List.of(1, 2)),
                environmentPlugin3
        );
    }

    private EnvironmentPluginState createEnvironmentPlugin(int pluginNumber, int checkNumber) {
        return createEnvironmentPlugin(pluginNumber, List.of(checkNumber));
    }

    private EnvironmentPluginState createEnvironmentPlugin(int pluginNumber, List<Integer> checkNumbers) {
        return EnvironmentPluginState.builder()
                .id(createPluginId(pluginNumber))
                .checks(
                        checkNumbers.stream()
                                .map(checkNumber -> createCheck(pluginNumber, checkNumber))
                                .collect(toUnmodifiableList())
                )
                .build();
    }

    private CheckState createCheck(int pluginNumber, int checkNumber) {
        return CheckState.builder()
                .name("test-check-name-" + pluginNumber + "-" + checkNumber)
                .build();
    }

    private String createPluginId(int pluginNumber) {
        return "test-plugin-id-" + pluginNumber;
    }

    @RequiredArgsConstructor
    private static class FakeEnvironmentPluginUpdateAction {

        private final EnvironmentPluginState updatedEnvironmentPlugin;
        private final List<EnvironmentPluginState> calls = new ArrayList<>();

        public EnvironmentPluginState apply(EnvironmentPluginState value) {
            calls.add(value);
            return updatedEnvironmentPlugin;
        }
    }
}
