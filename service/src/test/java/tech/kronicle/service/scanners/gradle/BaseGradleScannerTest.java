package tech.kronicle.service.scanners.gradle;

import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.SoftwareDependencyType;
import tech.kronicle.sdk.models.SoftwareRepository;
import tech.kronicle.service.constants.Comparators;
import tech.kronicle.service.scanners.BaseCodebaseScannerTest;
import tech.kronicle.service.scanners.gradle.internal.constants.MavenPackagings;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class BaseGradleScannerTest extends BaseCodebaseScannerTest {

    protected void assertThatGradleIsNotUsed(Component component) {
        assertThat(component.getGradle()).isNotNull();
        assertThat(component.getGradle().getUsed()).isFalse();
    }

    protected void assertThatGradleIsUsed(Component component) {
        assertThat(component.getGradle()).isNotNull();
        assertThat(component.getGradle().getUsed()).isTrue();
    }

    protected List<SoftwareRepository> getSoftwareRepositories(Component component) {
        return component.getSoftwareRepositories().stream()
                .sorted(Comparators.SOFTWARE_REPOSITORIES)
                .collect(Collectors.toList());
    }

    protected Map<SoftwareGroup, List<Software>> getSoftwareGroups(Component component) {
        Map<SoftwareGroup, List<Software>> softwareGroups = component
                .getSoftware()
                .stream()
                .sorted(Comparators.SOFTWARE)
                .collect(Collectors.groupingBy(this::softwareClassifier));

        List<Software> bomSoftware = softwareGroups.get(SoftwareGroup.BOM);

        if (nonNull(bomSoftware)) {
            bomSoftware.forEach(this::assertSoftwareFieldsAreValid);
        }

        List<Software> transitiveSoftware = softwareGroups.get(SoftwareGroup.TRANSITIVE);

        if (nonNull(transitiveSoftware)) {
            transitiveSoftware.forEach(this::assertSoftwareFieldsAreValid);
        }

        return softwareGroups;
    }

    private SoftwareGroup softwareClassifier(Software software) {
        if (Objects.equals(software.getPackaging(), MavenPackagings.BOM)) {
            return SoftwareGroup.BOM;
        } else if (Objects.equals(software.getDependencyType(), SoftwareDependencyType.DIRECT)) {
            return SoftwareGroup.DIRECT;
        } else if (Objects.equals(software.getDependencyType(), SoftwareDependencyType.TRANSITIVE)) {
            return SoftwareGroup.TRANSITIVE;
        } else {
            throw new RuntimeException("Unexpected software dependency type " + software.getDependencyType());
        }
    }

    private void assertSoftwareFieldsAreValid(Software item) {
        assertThat(item.getScannerId()).isNotEmpty();
        assertThat(item.getType()).isNotNull();
        assertThat(item.getDependencyType()).isNotNull();
        assertThat(item.getName()).isNotEmpty();
        assertThat(item.getVersion()).isNotEmpty();
        assertThat(item.getVersion()).doesNotContain("$");
    }
}
