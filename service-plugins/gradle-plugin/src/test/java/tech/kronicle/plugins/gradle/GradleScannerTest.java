package tech.kronicle.plugins.gradle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.gradlestaticanalyzer.GradleAnalysis;
import tech.kronicle.gradlestaticanalyzer.GradleStaticAnalyzer;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugintestutils.scanners.BaseCodebaseScannerTest;
import tech.kronicle.sdk.models.*;
import tech.kronicle.sdk.models.gradle.GradleState;
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
        underTest = new GradleScanner(gradleStaticAnalyzer, new ThrowableToScannerErrorMapper());
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
        Path codebaseDir = Path.of("test-repo");
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
        Path codebaseDir = Path.of("test-repo");
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
        Path codebaseDir = Path.of("test-repo");
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
        Path codebaseDir = Path.of("test-repo");
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
