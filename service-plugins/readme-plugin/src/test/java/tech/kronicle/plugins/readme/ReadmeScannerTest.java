package tech.kronicle.plugins.readme;

import tech.kronicle.sdk.models.readme.Readme;
import tech.kronicle.plugintestutils.scanners.BaseCodebaseScannerTest;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.readme.services.ReadmeFileNameChecker;
import tech.kronicle.pluginutils.utils.AntStyleIgnoreFileLoader;
import tech.kronicle.pluginutils.utils.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ReadmeScannerTest extends BaseCodebaseScannerTest {

    private ReadmeScanner underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new ReadmeScanner(new FileUtils(new AntStyleIgnoreFileLoader()), new ReadmeFileNameChecker());
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
        Output<Void> returnValue = underTest.scan(codebase);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        Readme readme = getMutatedComponent(returnValue).getReadme();
        assertThat(readme).isNull();
    }

    @Test
    public void scanShouldHandleSingleReadme() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("SingleReadme"));

        // When
        Output<Void> returnValue = underTest.scan(codebase);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        Readme readme = getMutatedComponent(returnValue).getReadme();
        assertThat(readme).isNotNull();
        assertThat(readme.getFileName()).isEqualTo("README.md");
        assertThat(readme.getContent()).isEqualTo("# Example Readme\nHere is the content\n");
    }

    @Test
    public void scanShouldHandleSingleReadmeInLowerCase() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("SingleReadmeInLowerCase"));

        // When
        Output<Void> returnValue = underTest.scan(codebase);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        Readme readme = getMutatedComponent(returnValue).getReadme();
        assertThat(readme).isNotNull();
        assertThat(readme.getFileName()).isEqualTo("readme.md");
        assertThat(readme.getContent()).isEqualTo("# Example Readme\nHere is the content\n");
    }

    @Test
    public void scanShouldIgnoreOtherFileTypes() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("OtherFileTypes"));

        // When
        Output<Void> returnValue = underTest.scan(codebase);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        Readme readme = getMutatedComponent(returnValue).getReadme();
        assertThat(readme).isNull();
    }

    @Test
    public void scanShouldIgnoreReadmeWithNoFileExtension() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("ReadmeWithNoFileExtension"));

        // When
        Output<Void> returnValue = underTest.scan(codebase);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        Readme readme = getMutatedComponent(returnValue).getReadme();
        assertThat(readme).isNull();
    }

    @Test
    public void scanShouldHandleMultipleReadmes() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("MultipleReadmes"));

        // When
        Output<Void> returnValue = underTest.scan(codebase);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        Readme readme = getMutatedComponent(returnValue).getReadme();
        assertThat(readme).isNotNull();
        assertThat(readme.getFileName()).isEqualTo("README.adoc");
        assertThat(readme.getContent()).isEqualTo("= Example Readme\n\nHere is the content\n");
    }

    @Test
    public void scanShouldIgnoreReadmeInSubdirectory() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("ReadmeInSubdirectory"));

        // When
        Output<Void> returnValue = underTest.scan(codebase);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        Readme readme = getMutatedComponent(returnValue).getReadme();
        assertThat(readme).isNull();
    }
}
