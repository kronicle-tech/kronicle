package tech.kronicle.plugins.github;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GitHubScannerTest {

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // Given
        GitHubScanner underTest = new GitHubScanner(null, null);

        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Find the status of any GitHub Actions build for a component's repo");
    }
}
