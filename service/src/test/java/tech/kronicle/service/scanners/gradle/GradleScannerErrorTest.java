package tech.kronicle.service.scanners.gradle;

import tech.kronicle.sdk.models.ScannerError;
import tech.kronicle.service.config.DownloadCacheConfig;
import tech.kronicle.service.config.UrlExistsCacheConfig;
import tech.kronicle.service.scanners.BaseCodebaseScannerTest;
import tech.kronicle.service.scanners.gradle.config.GradleConfig;
import tech.kronicle.service.scanners.models.Codebase;
import tech.kronicle.service.scanners.models.Output;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "download-cache.dir=build/test-data/tech.kronicle.service.scanners.gradle.GradleScannerErrorTest/download-cache", 
        "url-exists-cache.dir=build/test-data/tech.kronicle.service.scanners.gradle.GradleScannerErrorTest/url-exists-cache", 
        "gradle.pom-cache-dir=build/test-data/tech.kronicle.service.scanners.gradle.GradleScannerErrorTest/gradle/pom-cache"
})
@ContextConfiguration(classes = GradleScannerTestConfiguration.class)
@EnableConfigurationProperties(value = {DownloadCacheConfig.class, UrlExistsCacheConfig.class, GradleConfig.class})
public class GradleScannerErrorTest extends BaseCodebaseScannerTest {

    private static final String SCANNER_ID = "gradle";
    @Autowired
    private GradleScanner underTest;

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
