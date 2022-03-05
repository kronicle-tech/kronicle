package tech.kronicle.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicAuthUtilsTest {

    @Test
    public void basicAuthShouldCreateTheHeaderValueForBasicAuth() {
        // Given
        String username = "test-username";
        String password = "test-password";

        // When
        String returnValue = BasicAuthUtils.basicAuth(username, password);

        // Then
        assertThat(returnValue).isEqualTo("Basic dGVzdC11c2VybmFtZTp0ZXN0LXBhc3N3b3Jk");
    }
}
