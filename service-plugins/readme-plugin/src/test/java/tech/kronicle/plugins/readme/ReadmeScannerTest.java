package tech.kronicle.plugins.readme;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.readme.services.ReadmeFileNameChecker;
import tech.kronicle.plugintestutils.scanners.BaseCodebaseScannerTest;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.readme.ReadmeState;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.utils.FileUtilsFactory.createFileUtils;

public class ReadmeScannerTest extends BaseCodebaseScannerTest {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    private ReadmeScanner underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new ReadmeScanner(createFileUtils(), new ReadmeFileNameChecker());
    }

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("readme");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Scans a component's codebase for a README file at the root of the codebase");
    }

    @Test
    public void notesShouldReturnNull() {
        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void scanShouldHandleNoReadme() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("NoReadmes"));

        // When
        Output<Void, Component> returnValue = underTest.scan(codebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        ReadmeState readme = getReadme(returnValue);
        assertThat(readme).isNull();
    }

    @Test
    public void scanShouldHandleSingleReadme() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("SingleReadme"));

        // When
        Output<Void, Component> returnValue = underTest.scan(codebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        ReadmeState readme = getReadme(returnValue);
        assertThat(readme).isNotNull();
        assertThat(readme.getFileName()).isEqualTo("README.md");
        assertThat(readme.getContent()).isEqualTo("# Example Readme\nHere is the content\n");
    }

    @Test
    public void scanShouldHandleSingleReadmeInLowerCase() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("SingleReadmeInLowerCase"));

        // When
        Output<Void, Component> returnValue = underTest.scan(codebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        ReadmeState readme = getReadme(returnValue);
        assertThat(readme).isNotNull();
        assertThat(readme.getFileName()).isEqualTo("readme.md");
        assertThat(readme.getContent()).isEqualTo("# Example Readme\nHere is the content\n");
    }

    @Test
    public void scanShouldIgnoreOtherFileTypes() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("OtherFileTypes"));

        // When
        Output<Void, Component> returnValue = underTest.scan(codebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        ReadmeState readme = getReadme(returnValue);
        assertThat(readme).isNull();
    }

    @Test
    public void scanShouldIgnoreReadmeWithNoFileExtension() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("ReadmeWithNoFileExtension"));

        // When
        Output<Void, Component> returnValue = underTest.scan(codebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        ReadmeState readme = getReadme(returnValue);
        assertThat(readme).isNull();
    }

    @Test
    public void scanShouldHandleMultipleReadmes() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("MultipleReadmes"));

        // When
        Output<Void, Component> returnValue = underTest.scan(codebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        ReadmeState readme = getReadme(returnValue);
        assertThat(readme).isNotNull();
        assertThat(readme.getFileName()).isEqualTo("README.adoc");
        assertThat(readme.getContent()).isEqualTo("= Example Readme\n\nHere is the content\n");
    }

    @Test
    public void scanShouldIgnoreReadmeInSubdirectory() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("ReadmeInSubdirectory"));

        // When
        Output<Void, Component> returnValue = underTest.scan(codebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        ReadmeState readme = getReadme(returnValue);
        assertThat(readme).isNull();
    }

    protected ReadmeState getReadme(Output<Void, Component> returnValue) {
        return getMutatedComponent(returnValue).getState(ReadmeState.TYPE);
    }
}
