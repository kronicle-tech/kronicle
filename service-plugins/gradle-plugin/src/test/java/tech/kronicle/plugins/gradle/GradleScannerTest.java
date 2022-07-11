package tech.kronicle.plugins.gradle;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.gradle.services.DependencyFileAnalyzer;
import tech.kronicle.plugins.gradle.services.GradleDependenciesFinder;
import tech.kronicle.plugins.gradle.services.GradleWrapperFinder;
import tech.kronicle.plugintestutils.scanners.BaseCodebaseScannerTest;
import tech.kronicle.sdk.models.*;
import tech.kronicle.sdk.models.gradle.GradleState;
import tech.kronicle.utils.AntStyleIgnoreFileLoader;
import tech.kronicle.utils.FileUtils;
import tech.kronicle.utils.ThrowableToScannerErrorMapper;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GradleScannerTest extends BaseCodebaseScannerTest {

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private GradleScanner underTest;

    @BeforeEach
    public void beforeEach() {
        FileUtils fileUtils = new FileUtils(new AntStyleIgnoreFileLoader());
        underTest = new GradleScanner(
                new GradleDependenciesFinder(
                        new DependencyFileAnalyzer(fileUtils, new YAMLMapper()),
                        new GradleWrapperFinder(fileUtils)
                ),
                new ThrowableToScannerErrorMapper()
        );
    }

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("gradle");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Scans a component's codebase for any Gradle build scripts and collects information like Gradle version and "
                + "software used");
    }

    @Test
    public void notesShouldReturnTheNotesForTheScanner() {
        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isEqualTo("If the scanner finds Gradle build scripts, it will:\n"
                + "\n"
                + "* Find the version of Gradle wrapper used\n"
                + "* Find the names and versions of any Gradle plugins used"
                + "* Find the names and versions of any Java libraries used");
    }

    @Test
    public void scanShouldSetGradleIsNotUsedWhenGradleIsNotUsedInCodebase() {
        // Given
        Path codebaseDir = getCodebaseDir("Empty");
        Codebase input = new Codebase(new RepoReference("https://example.com/test-repo"), codebaseDir);

        // When
        Output<Void, Component> returnValue = underTest.scan(input);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        Component component = getMutatedComponent(returnValue);
        assertThat(getGradle(component).getUsed()).isFalse();
        assertNoSoftwareRepositoriesState(component);
        assertNoSoftwaresState(component);
    }

    @Test
    public void scanShouldFindDependenciesInAGradleDependenciesYamlFile() {
        // Given
        Path codebaseDir = getCodebaseDir("DependenciesFile");
        Codebase input = new Codebase(new RepoReference("https://example.com/test-repo"), codebaseDir);

        // When
        Output<Void, Component> returnValue = underTest.scan(input);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        Component component = getMutatedComponent(returnValue);
        assertThat(getGradle(component).getUsed()).isTrue();
        assertNoSoftwareRepositoriesState(component);
        assertThat(getSoftwares(component)).containsExactlyInAnyOrder(
                createSoftware("annotationProcessor", SoftwareDependencyType.DIRECT, "org.projectlombok:lombok", "1.18.24"),
                createSoftware("compileClasspath", SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
                createSoftware("compileClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-annotations", "2.13.3"),
                createSoftware("compileClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson:jackson-bom", "2.13.3"),
                createSoftware("compileClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-core", "2.13.3"),
                createSoftware("compileClasspath", SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml", "2.13.3"),
                createSoftware("compileClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
                createSoftware("compileClasspath", SoftwareDependencyType.TRANSITIVE, "org.yaml:snakeyaml", "1.30"),
                createSoftware("compileClasspath", SoftwareDependencyType.DIRECT, "org.projectlombok:lombok", "1.18.24"),
                createSoftware("default", SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
                createSoftware("default", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-annotations", "2.13.3"),
                createSoftware("default", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson:jackson-bom", "2.13.3"),
                createSoftware("default", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-core", "2.13.3"),
                createSoftware("default", SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml", "2.13.3"),
                createSoftware("default", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
                createSoftware("default", SoftwareDependencyType.TRANSITIVE, "org.yaml:snakeyaml", "1.30"),
                createSoftware("lombok", SoftwareDependencyType.DIRECT, "org.projectlombok:lombok", "1.18.24"),
                createSoftware("runtimeClasspath", SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
                createSoftware("runtimeClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-annotations", "2.13.3"),
                createSoftware("runtimeClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson:jackson-bom", "2.13.3"),
                createSoftware("runtimeClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-core", "2.13.3"),
                createSoftware("runtimeClasspath", SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml", "2.13.3"),
                createSoftware("runtimeClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
                createSoftware("runtimeClasspath", SoftwareDependencyType.TRANSITIVE, "org.yaml:snakeyaml", "1.30"),
                createSoftware("testAnnotationProcessor", SoftwareDependencyType.DIRECT, "org.projectlombok:lombok", "1.18.24"),
                createSoftware("testCompileClasspath", SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
                createSoftware("testCompileClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-annotations", "2.13.3"),
                createSoftware("testCompileClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson:jackson-bom", "2.13.3"),
                createSoftware("testCompileClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-core", "2.13.3"),
                createSoftware("testCompileClasspath", SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml", "2.13.3"),
                createSoftware("testCompileClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
                createSoftware("testCompileClasspath", SoftwareDependencyType.TRANSITIVE, "org.yaml:snakeyaml", "1.30"),
                createSoftware("testCompileClasspath", SoftwareDependencyType.DIRECT, "org.projectlombok:lombok", "1.18.24"),
                createSoftware("testRuntimeClasspath", SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
                createSoftware("testRuntimeClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-annotations", "2.13.3"),
                createSoftware("testRuntimeClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson:jackson-bom", "2.13.3"),
                createSoftware("testRuntimeClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-core", "2.13.3"),
                createSoftware("testRuntimeClasspath", SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml", "2.13.3"),
                createSoftware("testRuntimeClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
                createSoftware("testRuntimeClasspath", SoftwareDependencyType.TRANSITIVE, "org.yaml:snakeyaml", "1.30")
        );
    }

    @Test
    public void scanShouldFindDependenciesInMultipleGradleDependenciesYamlFiles() {
        // Given
        Path codebaseDir = getCodebaseDir("MultipleDependenciesFiles");
        Codebase input = new Codebase(new RepoReference("https://example.com/test-repo"), codebaseDir);

        // When
        Output<Void, Component> returnValue = underTest.scan(input);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        Component component = getMutatedComponent(returnValue);
        assertThat(getGradle(component).getUsed()).isTrue();
        assertNoSoftwareRepositoriesState(component);
        assertThat(getSoftwares(component)).containsExactlyInAnyOrder(
            createSoftware("annotationProcessor", SoftwareDependencyType.DIRECT, "org.projectlombok:lombok", "1.18.24"),
            createSoftware("compileClasspath", SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
            createSoftware("compileClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-annotations", "2.13.3"),
            createSoftware("compileClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-core", "2.13.3"),
            createSoftware("compileClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson:jackson-bom", "2.13.3"),
            createSoftware("compileClasspath", SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml", "2.13.3"),
            createSoftware("compileClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
            createSoftware("compileClasspath", SoftwareDependencyType.TRANSITIVE, "org.yaml:snakeyaml", "1.30"),
            createSoftware("compileClasspath", SoftwareDependencyType.DIRECT, "org.projectlombok:lombok", "1.18.24"),
            createSoftware("default", SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
            createSoftware("default", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-annotations", "2.13.3"),
            createSoftware("default", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-core", "2.13.3"),
            createSoftware("default", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson:jackson-bom", "2.13.3"),
            createSoftware("default", SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml", "2.13.3"),
            createSoftware("default", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
            createSoftware("default", SoftwareDependencyType.TRANSITIVE, "org.yaml:snakeyaml", "1.30"),
            createSoftware("lombok", SoftwareDependencyType.DIRECT, "org.projectlombok:lombok", "1.18.24"),
            createSoftware("runtimeClasspath", SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
            createSoftware("runtimeClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-annotations", "2.13.3"),
            createSoftware("runtimeClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-core", "2.13.3"),
            createSoftware("runtimeClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson:jackson-bom", "2.13.3"),
            createSoftware("runtimeClasspath", SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml", "2.13.3"),
            createSoftware("runtimeClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
            createSoftware("runtimeClasspath", SoftwareDependencyType.TRANSITIVE, "org.yaml:snakeyaml", "1.30"),
            createSoftware("testAnnotationProcessor", SoftwareDependencyType.DIRECT, "org.projectlombok:lombok", "1.18.24"),
            createSoftware("testCompileClasspath", SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
            createSoftware("testCompileClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-annotations", "2.13.3"),
            createSoftware("testCompileClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-core", "2.13.3"),
            createSoftware("testCompileClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson:jackson-bom", "2.13.3"),
            createSoftware("testCompileClasspath", SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml", "2.13.3"),
            createSoftware("testCompileClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
            createSoftware("testCompileClasspath", SoftwareDependencyType.TRANSITIVE, "org.yaml:snakeyaml", "1.30"),
            createSoftware("testCompileClasspath", SoftwareDependencyType.DIRECT, "org.projectlombok:lombok", "1.18.24"),
            createSoftware("testRuntimeClasspath", SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
            createSoftware("testRuntimeClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-annotations", "2.13.3"),
            createSoftware("testRuntimeClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-core", "2.13.3"),
            createSoftware("testRuntimeClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson:jackson-bom", "2.13.3"),
            createSoftware("testRuntimeClasspath", SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml", "2.13.3"),
            createSoftware("testRuntimeClasspath", SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
            createSoftware("testRuntimeClasspath", SoftwareDependencyType.TRANSITIVE, "org.yaml:snakeyaml", "1.30")
        );
    }

    private Software createSoftware(
            String scope,
            SoftwareDependencyType dependencyType,
            String name,
            String version
    ) {
        return Software.builder()
                .scannerId("gradle")
                .scope(scope)
                .dependencyType(dependencyType)
                .name(name)
                .version(version)
                .build();
    }

    private GradleState getGradle(Component component) {
        GradleState state = component.getState(GradleState.TYPE);
        assertThat(state).isNotNull();
        return state;
    }

    private List<Software> getSoftwares(Component component) {
        SoftwaresState state = component.getState(SoftwaresState.TYPE);
        assertThat(state).isNotNull();
        return state.getSoftwares();
    }

    private void assertNoSoftwareRepositoriesState(Component component) {
        SoftwaresState state = component.getState("software-repositories");
        assertThat(state).isNull();
    }

    private void assertNoSoftwaresState(Component component) {
        SoftwaresState state = component.getState("softwares");
        assertThat(state).isNull();
    }
}
