package tech.kronicle.service.scanners.gradle;

import tech.kronicle.sdk.models.Repo;
import tech.kronicle.service.config.DownloadCacheConfig;
import tech.kronicle.service.config.UrlExistsCacheConfig;
import tech.kronicle.service.scanners.gradle.config.GradleConfig;
import tech.kronicle.service.scanners.models.Codebase;
import tech.kronicle.service.scanners.models.Output;
import org.junit.jupiter.apiqgit a.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "download-cache.dir=build/test-data/tech.kronicle.service.scanners.gradle.RealCodebaseTest/download-cache",
        "url-exists-cache.dir=build/test-data/tech.kronicle.service.scanners.gradle.RealCodebaseTest/url-exists-cache",
        "gradle.pom-cache-dir=build/test-data/tech.kronicle.service.scanners.gradle.RealCodebaseTest/gradle/pom-cache"
})
@ContextConfiguration(classes = GradleScannerTestConfiguration.class)
@EnableConfigurationProperties(value = {DownloadCacheConfig.class, UrlExistsCacheConfig.class, GradleConfig.class})
public class RealCodebaseTest {

    private static final Repo TEST_REPO = new Repo("test");
    @Autowired
    private GradleScanner underTest;

    @TestFactory
    Stream<DynamicTest> shouldSuccessfullyScanCodebases() throws IOException {
        String tmpDir = System.getenv("TMPDIR");

        if (isNull(tmpDir)) {
            return createDevOnlyMessageTest();
        }

        Path reposDir = Path.of(tmpDir).resolve("kronicle-service/data/git/repos");

        if (!Files.exists(reposDir)) {
            return createDevOnlyMessageTest();
        }

        return Files.find(reposDir, 1, matchRepoDirs(reposDir))
                .map(this::createDynamicTestShouldSuccessfullyScanCodebase);
    }

    private BiPredicate<Path, BasicFileAttributes> matchRepoDirs(Path reposDir) {
        return (path, basicFileAttributes) -> path != reposDir
                && basicFileAttributes.isDirectory();
    }

    private Stream<DynamicTest> createDevOnlyMessageTest() {
        return Stream.of(dynamicTest("Test only runs on development machines", () -> { }));
    }

    private DynamicTest createDynamicTestShouldSuccessfullyScanCodebase(Path codebaseDir) {
        return dynamicTest(codebaseDir.getFileName().toString(), () -> shouldSuccessfullyScanCodebase(codebaseDir));
    }

    private void shouldSuccessfullyScanCodebase(Path codebaseDir) {
        // Given
        Codebase codebase = new Codebase(TEST_REPO, codebaseDir);

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThatNoOutputExceptions(output);
    }

    private void assertThatNoOutputExceptions(Output<Void> output) {
        assertThat(output.getErrors()).isNotNull();

        if (output.getErrors().size() > 0) {
            throw new RuntimeException(output.getErrors().get(0).toString());
        }
    }
}
