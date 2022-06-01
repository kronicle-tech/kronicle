package tech.kronicle.plugins.nodejs;

import org.slf4j.Logger;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugintestutils.scanners.BaseCodebaseScannerTest;
import tech.kronicle.sdk.models.*;
import tech.kronicle.sdk.models.nodejs.NodeJsState;
import tech.kronicle.utils.Comparators;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseNodeJsScannerTest extends BaseCodebaseScannerTest {

    protected abstract Logger log();

    protected void assertThatNodeJsIsNotUsed(Component component) {
        NodeJsState nodeJs = getNodeJs(component);
        assertThat(nodeJs.getUsed()).isFalse();
    }

    protected void assertThatNodeJsIsUsed(Component component) {
        NodeJsState nodeJs = getNodeJs(component);
        assertThat(nodeJs.getUsed()).isTrue();
    }

    protected void assertNoState(Output<Void, Component> returnValue) {
        assertThat(getMutatedComponent(returnValue).getStates()).isEmpty();
    }

    protected NodeJsState getNodeJs(Component component) {
        NodeJsState state = component.getState(NodeJsState.TYPE);
        assertThat(state).isNotNull();
        return state;
    }

    protected List<Software> getSoftware(Component component) {
        SoftwaresState state = component
                .getState(SoftwaresState.TYPE);
        List<Software> software = state.getSoftwares().stream()
                .sorted(Comparators.SOFTWARE)
                .collect(Collectors.toList());
        logSoftware(software);
        return software;
    }

    private void logSoftware(List<Software> software) {
        log().info(software.stream()
                .map(it -> {
                    StringBuilder builder = new StringBuilder().append("\nSoftware.builder().scannerId(\"nodejs\").name(\"")
                            .append(it.getName())
                            .append("\").version(\"")
                            .append(it.getVersion())
                            .append("\").packaging(\"npm-package\")")
                            .append(".dependencyType(SoftwareDependencyType.")
                            .append(it.getDependencyType().name())
                            .append(")");
                    if (nonNull(it.getScope())) {
                        builder.append(".scope(SoftwareScope.")
                                .append(it.getScope().name())
                                .append(")");
                    }
                    return builder
                            .append(".build(),\n")
                            .toString();
                })
                .collect(Collectors.joining()));
    }
}
