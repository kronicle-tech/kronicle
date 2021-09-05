package tech.kronicle.sdk.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RepoTest {

    @Test
    public void referenceShouldReturnUrl() {
        // Given
        Repo underTest = new Repo("https://example.com/example.git");

        // When
        String returnValue = underTest.reference();

        // Then
        assertThat(returnValue).isEqualTo("https://example.com/example.git");
    }
}
