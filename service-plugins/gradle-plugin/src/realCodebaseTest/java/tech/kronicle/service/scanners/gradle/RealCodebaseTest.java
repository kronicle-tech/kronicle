package tech.kronicle.service.scanners.gradle;

import io.micronaut.context.annotation.Bean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import javax.inject.Inject;

import org.springframework.web.reactive.function.client.WebClient;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.service.scanners.gradle.config.DownloadCacheConfig;
import tech.kronicle.service.scanners.gradle.config.DownloaderConfig;
import tech.kronicle.service.scanners.gradle.config.GradleConfig;
import tech.kronicle.service.scanners.gradle.config.PomCacheConfig;
import tech.kronicle.service.scanners.gradle.config.UrlExistsCacheConfig;
import tech.kronicle.service.scanners.gradle.internal.services.DownloadCache;
import tech.kronicle.service.scanners.gradle.internal.services.Downloader;
import tech.kronicle.service.scanners.gradle.internal.services.HttpRequestMaker;
import tech.kronicle.service.scanners.gradle.internal.services.UrlExistsCache;
import tech.kronicle.service.scanners.gradle.testbeans.TestDataDir;
import tech.kronicle.service.scanners.models.Codebase;
import tech.kronicle.service.scanners.models.Output;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import tech.kronicle.service.scanners.services.ThrowableToScannerErrorMapper;
import tech.kronicle.service.utils.AntStyleIgnoreFileLoader;
import tech.kronicle.service.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@MicronautTest
public class RealCodebaseTest {

    private static final Repo TEST_REPO = new Repo("test");
    @Inject
    private GradleScanner underTest;

    @Bean
    public TestDataDir testDataDir() {
        return new TestDataDir(this.getClass());
    }

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
