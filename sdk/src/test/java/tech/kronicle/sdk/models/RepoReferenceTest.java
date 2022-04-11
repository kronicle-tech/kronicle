package tech.kronicle.sdk.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RepoReferenceTest {

    @Test
    public void referenceShouldReturnUrl() {
        // Given
        RepoReference underTest = new RepoReference("https://example.com/example.git");

        // When
        String returnValue = underTest.reference();

        // Then
        assertThat(returnValue).isEqualTo("https://example.com/example.git");
    }
}
