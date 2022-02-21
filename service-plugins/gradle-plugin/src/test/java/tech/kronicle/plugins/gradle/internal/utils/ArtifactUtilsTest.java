package tech.kronicle.plugins.gradle.internal.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ArtifactUtilsTest {

    private final ArtifactUtils underTest = new ArtifactUtils();

    @Test
    public void getArtifactPartsShouldExtractGroupIdAndArtifactIdAndNameAndVersion() {
        // Given
        String artifact = "group:artifact-id:version";

        // When
        ArtifactUtils.ArtifactParts returnValue = underTest.getArtifactParts(artifact);

        // Then
        assertThat(returnValue.getGroupId()).isEqualTo("group");
        assertThat(returnValue.getArtifactId()).isEqualTo("artifact-id");
        assertThat(returnValue.getName()).isEqualTo("group:artifact-id");
        assertThat(returnValue.getVersion()).isEqualTo("version");
        assertThat(returnValue.getPackaging()).isNull();
    }

    @Test
    public void getArtifactPartsShouldCheckForNull() {
        // Given
        String artifact = null;

        // When
        Throwable thrown = catchThrowable(() -> underTest.getArtifactParts(artifact));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("artifact");
    }

    @Test
    public void getArtifactPartsShouldCheckForThreeParts() {
        // Given
        String artifact = "group:artifact-id";

        // When
        Throwable thrown = catchThrowable(() -> underTest.getArtifactParts(artifact));

        // Then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertThat(thrown).hasMessage("artifact must contain at least 3 parts");
    }

    @Test
    public void getArtifactPartsShouldExtractPackaging() {
        // Given
        String artifact = "group:artifact-id:version:packaging";

        // When
        ArtifactUtils.ArtifactParts returnValue = underTest.getArtifactParts(artifact);

        // Then
        assertThat(returnValue.getGroupId()).isEqualTo("group");
        assertThat(returnValue.getArtifactId()).isEqualTo("artifact-id");
        assertThat(returnValue.getName()).isEqualTo("group:artifact-id");
        assertThat(returnValue.getVersion()).isEqualTo("version");
        assertThat(returnValue.getPackaging()).isEqualTo("packaging");
    }
}
