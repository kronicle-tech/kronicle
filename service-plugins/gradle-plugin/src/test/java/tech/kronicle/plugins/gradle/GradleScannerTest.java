package tech.kronicle.plugins.gradle;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.gradlestaticanalyzer.GradleAnalysis;
import tech.kronicle.gradlestaticanalyzer.GradleStaticAnalyzer;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.gradle.services.DependencyFileAnalyzer;
import tech.kronicle.plugins.gradle.services.GradleDependenciesFinder;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GradleScannerTest extends BaseCodebaseScannerTest {

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    public static final String SCANNER_ID = "gradle";
    private GradleScanner underTest;
    @Mock
    private GradleStaticAnalyzer gradleStaticAnalyzer;

    @BeforeEach
    public void beforeEach() {
        underTest = new GradleScanner(
                new GradleDependenciesFinder(
                        new DependencyFileAnalyzer(new FileUtils(new AntStyleIgnoreFileLoader()), new YAMLMapper()),
                        gradleStaticAnalyzer
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
    public void scanShouldSetGradleIsUsedWhenGradleIsUsedInCodebase() {
        // Given
        Path codebaseDir = getCodebaseDir("Empty");
        Codebase input = new Codebase(new RepoReference("https://example.com/test-repo"), codebaseDir);
        GradleAnalysis gradleAnalysis = new GradleAnalysis(
                true,
                List.of(),
                List.of()
        );
        when(gradleStaticAnalyzer.analyzeCodebase(codebaseDir)).thenReturn(gradleAnalysis);

        // When
        Output<Void, Component> returnValue = underTest.scan(input);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        Component component = getMutatedComponent(returnValue);
        assertThat(getGradle(component).getUsed()).isTrue();
        assertNoSoftwareRepositoriesState(component);
        assertNoSoftwaresState(component);
    }

    @Test
    public void scanShouldSetGradleIsNotUsedWhenGradleIsNotUsedInCodebase() {
        // Given
        Path codebaseDir = getCodebaseDir("Empty");
        Codebase input = new Codebase(new RepoReference("https://example.com/test-repo"), codebaseDir);
        GradleAnalysis gradleAnalysis = new GradleAnalysis(
                false,
                List.of(),
                List.of()
        );
        when(gradleStaticAnalyzer.analyzeCodebase(codebaseDir)).thenReturn(gradleAnalysis);

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
    public void scanShouldSetScannerIdOnAnySoftwareRepositories() {
        // Given
        Path codebaseDir = getCodebaseDir("Empty");
        Codebase input = new Codebase(new RepoReference("https://example.com/test-repo"), codebaseDir);
        SoftwareRepository softwareRepository1 = SoftwareRepository.builder().url("https://example.com/test-maven-repo-1").build();
        SoftwareRepository softwareRepository2 = SoftwareRepository.builder().url("https://example.com/test-maven-repo-2").build();
        GradleAnalysis gradleAnalysis = new GradleAnalysis(
                true,
                List.of(
                        softwareRepository1,
                        softwareRepository2
                ),
                List.of()
        );
        when(gradleStaticAnalyzer.analyzeCodebase(codebaseDir)).thenReturn(gradleAnalysis);

        // When
        Output<Void, Component> returnValue = underTest.scan(input);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        Component component = getMutatedComponent(returnValue);
        assertThat(getGradle(component).getUsed()).isTrue();
        assertThat(getSoftwareRepositories(component)).containsExactly(
                softwareRepository1.withScannerId(SCANNER_ID),
                softwareRepository2.withScannerId(SCANNER_ID)
        );
        assertNoSoftwaresState(component);
    }

    @Test
    public void scanShouldSetScannerIdOnAnySoftware() {
        // Given
        Path codebaseDir = getCodebaseDir("Empty");
        Codebase input = new Codebase(new RepoReference("https://example.com/test-repo"), codebaseDir);
        Software software1 = Software.builder().name("test-software-1").build();
        Software software2 = Software.builder().name("test-software-2").build();
        GradleAnalysis gradleAnalysis = new GradleAnalysis(
                true,
                List.of(),
                List.of(
                        software1,
                        software2
                )
        );
        when(gradleStaticAnalyzer.analyzeCodebase(codebaseDir)).thenReturn(gradleAnalysis);

        // When
        Output<Void, Component> returnValue = underTest.scan(input);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        Component component = getMutatedComponent(returnValue);
        assertThat(getGradle(component).getUsed()).isTrue();
        assertNoSoftwareRepositoriesState(component);
        assertThat(getSoftwares(component)).containsExactly(
                software1.withScannerId(SCANNER_ID),
                software2.withScannerId(SCANNER_ID)
        );
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
        assertThat(getSoftwares(component)).containsExactly(
                createSoftware(SoftwareDependencyType.DIRECT, "org.projectlombok:lombok", "1.18.24"),
                createSoftware(SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-annotations", "2.13.3"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson:jackson-bom", "2.13.3"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-core", "2.13.3"),
                createSoftware(SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml", "2.13.3"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "org.yaml:snakeyaml", "1.30")
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
        assertThat(getSoftwares(component)).containsExactly(
                createSoftware(SoftwareDependencyType.DIRECT, "org.projectlombok:lombok", "1.18.24"),
                createSoftware(SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-annotations", "2.13.3"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson:jackson-bom", "2.13.3"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-core", "2.13.3"),
                createSoftware(SoftwareDependencyType.DIRECT, "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml", "2.13.3"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "com.fasterxml.jackson.core:jackson-databind", "2.13.3"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "org.yaml:snakeyaml", "1.30"),
                createSoftware(SoftwareDependencyType.DIRECT, "org.springframework.boot:spring-boot-dependencies", "2.7.1"),
                createSoftware(SoftwareDependencyType.DIRECT, "org.springframework.cloud:spring-cloud-dependencies", "2021.0.3"),
                createSoftware(SoftwareDependencyType.DIRECT, "org.springframework:spring-core", "5.3.21"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "org.springframework:spring-jcl", "5.3.21"),
                createSoftware(SoftwareDependencyType.DIRECT, "tech.kronicle:sdk", "0.0.1"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "jakarta.validation:jakarta.validation-api", "2.0.2"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "org.hibernate.validator:hibernate-validator", "6.2.3.Final"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "com.fasterxml:classmate", "1.5.1"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "org.jboss.logging:jboss-logging", "3.4.3.Final"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "org.springframework.boot:spring-boot-dependencies", "2.7.1"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "org.springframework.cloud:spring-cloud-dependencies", "2021.0.3"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "tech.kronicle:graphql-java-code-first-annotations", "0.0.1"),
                createSoftware(SoftwareDependencyType.DIRECT, "org.jacoco:org.jacoco.agent", "0.8.7"),
                createSoftware(SoftwareDependencyType.DIRECT, "org.jacoco:org.jacoco.ant", "0.8.7"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "org.jacoco:org.jacoco.agent", "0.8.7"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "org.jacoco:org.jacoco.core", "0.8.7"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "org.ow2.asm:asm-commons", "9.1"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "org.ow2.asm:asm-analysis", "9.1"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "org.ow2.asm:asm-tree", "9.1"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "org.ow2.asm:asm", "9.1"),
                createSoftware(SoftwareDependencyType.TRANSITIVE, "org.jacoco:org.jacoco.report", "0.8.7")
        );
    }

    private Software createSoftware(SoftwareDependencyType dependencyType, String name, String version) {
        return Software.builder()
                .scannerId("gradle")
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

    private List<SoftwareRepository> getSoftwareRepositories(Component component) {
        SoftwareRepositoriesState state = component.getState(SoftwareRepositoriesState.TYPE);
        assertThat(state).isNotNull();
        return state.getSoftwareRepositories();
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
