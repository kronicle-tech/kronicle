package tech.kronicle.service.scanners.gradle;

import io.micronaut.context.annotation.Bean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.ScannerError;
import tech.kronicle.service.scanners.gradle.config.DownloadCacheConfig;
import tech.kronicle.service.scanners.gradle.config.GradleConfig;
import tech.kronicle.service.scanners.gradle.config.PomCacheConfig;
import tech.kronicle.service.scanners.gradle.config.UrlExistsCacheConfig;
import tech.kronicle.service.scanners.gradle.testbeans.TestDataDir;
import tech.kronicle.service.scanners.models.Codebase;
import tech.kronicle.service.scanners.models.Output;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
public class GradleScannerErrorTest extends BaseGradleScannerTest {

    private static final String SCANNER_ID = "gradle";

    @Inject
    private GradleScanner underTest;

    @Bean
    public TestDataDir testDataDir() {
        return new TestDataDir(this.getClass());
    }

    @Test
    public void shouldScanBuildscriptMissingRepositoryBuild() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("BuildscriptMissingRepository"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        assertThat(output.getComponentTransformer()).isNull();
        assertThat(output.getErrors()).hasSize(1);
        ScannerError error;
        error = output.getErrors().get(0);
        assertThat(error.getScannerId()).isEqualTo(SCANNER_ID);
        assertThat(error.getMessage()).isEqualTo("Failed to scan codebase");
        assertThat(error.getCause()).isNotNull();
        assertThat(error.getCause().getScannerId()).isEqualTo(SCANNER_ID);
        assertThat(error.getCause().getMessage()).containsPattern("^Failed to process build file \\\"[^\\\"]+/service/src/test/resources/tech/kronicle/service/scanners/gradle/GradleScannerErrorTest/BuildscriptMissingRepository/build.gradle\\\" for THIS_PROJECT project mode and BUILDSCRIPT_DEPENDENCIES process phase$");
        assertThat(error.getCause().getCause()).isNotNull();
        assertThat(error.getCause().getCause().getScannerId()).isEqualTo(SCANNER_ID);
        assertThat(error.getCause().getCause().getMessage()).isEqualTo("Failed to create software item for artifact");
        assertThat(error.getCause().getCause().getCause()).isNotNull();
        assertThat(error.getCause().getCause().getCause().getScannerId()).isEqualTo(SCANNER_ID);
        assertThat(error.getCause().getCause().getCause().getMessage()).isEqualTo("No safe repositories configured");
        assertThat(error.getCause().getCause().getCause().getCause()).isNull();
    }

    @Test
    public void shouldScanBuildscriptMissingRepositoryWithEmptyPluginsBuild() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("BuildscriptMissingRepositoryWithEmptyPlugins"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        assertThat(output.getComponentTransformer()).isNull();
        assertThat(output.getErrors()).hasSize(1);
        ScannerError error;
        error = output.getErrors().get(0);
        assertThat(error.getScannerId()).isEqualTo(SCANNER_ID);
        assertThat(error.getMessage()).isEqualTo("Failed to scan codebase");
        assertThat(error.getCause()).isNotNull();
        assertThat(error.getCause().getScannerId()).isEqualTo(SCANNER_ID);
        assertThat(error.getCause().getMessage()).containsPattern("^Failed to process build file \\\"[^\\\"]+/service/src/test/resources/tech/kronicle/service/scanners/gradle/GradleScannerErrorTest/BuildscriptMissingRepositoryWithEmptyPlugins/build.gradle\\\" for THIS_PROJECT project mode and BUILDSCRIPT_DEPENDENCIES process phase$");
        assertThat(error.getCause().getCause()).isNotNull();
        assertThat(error.getCause().getCause().getScannerId()).isEqualTo(SCANNER_ID);
        assertThat(error.getCause().getCause().getMessage()).isEqualTo("Failed to create software item for artifact");
        assertThat(error.getCause().getCause().getCause()).isNotNull();
        assertThat(error.getCause().getCause().getCause().getScannerId()).isEqualTo(SCANNER_ID);
        assertThat(error.getCause().getCause().getCause().getMessage()).isEqualTo("No safe repositories configured");
        assertThat(error.getCause().getCause().getCause().getCause()).isNull();
    }
}
