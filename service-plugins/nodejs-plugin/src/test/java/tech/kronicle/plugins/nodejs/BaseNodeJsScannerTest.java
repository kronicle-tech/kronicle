package tech.kronicle.plugins.nodejs;

import org.slf4j.Logger;
import tech.kronicle.plugintestutils.scanners.BaseCodebaseScannerTest;
import tech.kronicle.pluginutils.constants.Comparators;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.SoftwareRepository;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseNodeJsScannerTest extends BaseCodebaseScannerTest {

    protected abstract Logger log();

    protected void assertThatNodeJsIsNotUsed(Component component) {
        assertThat(component.getNodeJs()).isNotNull();
        assertThat(component.getNodeJs().getUsed()).isFalse();
    }

    protected void assertThatNodeJsIsUsed(Component component) {
        assertThat(component.getNodeJs()).isNotNull();
        assertThat(component.getNodeJs().getUsed()).isTrue();
    }

    protected List<SoftwareRepository> getSoftwareRepositories(Component component) {
        return component.getSoftwareRepositories().stream()
                .sorted(Comparators.SOFTWARE_REPOSITORIES)
                .collect(Collectors.toList());
    }

    protected List<Software> getSoftware(Component component) {
        List<Software> software = component
                .getSoftware()
                .stream()
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
