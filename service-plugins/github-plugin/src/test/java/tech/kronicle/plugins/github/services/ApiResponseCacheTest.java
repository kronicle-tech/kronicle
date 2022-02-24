package tech.kronicle.plugins.github.services;

import lombok.Value;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.github.config.GitHubAccessTokenConfig;
import tech.kronicle.plugins.github.models.ApiResponseCacheEntry;
import tech.kronicle.plugins.github.services.ApiResponseCache;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiResponseCacheTest {

    private static final GitHubAccessTokenConfig ACCESS_TOKEN =
            new GitHubAccessTokenConfig("test-user", "test-personal-access-token");
    private static final GitHubAccessTokenConfig DIFFERENT_ACCESS_TOKEN =
            new GitHubAccessTokenConfig("different-user", "different-personal-access-token");
    private static final String URI = "http://example.com/test-uri";
    private static final String ETAG = "test-etag";
    private static final TestResponse TEST_RESPONSE = new TestResponse("test-response");
    private final ApiResponseCache underTest = new ApiResponseCache();

    @Test
    public void getEntryShouldReturnNullWhenEntryIsNotCached() {
        // When
        ApiResponseCacheEntry<TestResponse> returnValue = underTest.getEntry(ACCESS_TOKEN, URI);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getEntryShouldReturnExistingEntryWhenAlreadyCached() {
        // Given
        ApiResponseCacheEntry<TestResponse> entry = new ApiResponseCacheEntry<>(ETAG, TEST_RESPONSE);
        underTest.putEntry(ACCESS_TOKEN, URI, entry);

        // When
        ApiResponseCacheEntry<TestResponse> returnValue = underTest.getEntry(ACCESS_TOKEN, URI);

        // Then
        assertThat(returnValue).isSameAs(entry);
    }

    @Test
    public void getEntryShouldNotReturnExistingEntryWhenUsernameIsDifferent() {
        // Given
        ApiResponseCacheEntry<TestResponse> entry = new ApiResponseCacheEntry<>(ETAG, TEST_RESPONSE);
        underTest.putEntry(ACCESS_TOKEN, URI, entry);

        // When
        ApiResponseCacheEntry<TestResponse> returnValue = underTest.getEntry(DIFFERENT_ACCESS_TOKEN, URI);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getEntryShouldNotReturnExistingEntryWhenUriIsDifferent() {
        // Given
        ApiResponseCacheEntry<TestResponse> entry = new ApiResponseCacheEntry<>(ETAG, TEST_RESPONSE);
        underTest.putEntry(ACCESS_TOKEN, URI, entry);

        // When
        ApiResponseCacheEntry<TestResponse> returnValue = underTest.getEntry(ACCESS_TOKEN, "https://example.com/different-uri");

        // Then
        assertThat(returnValue).isNull();
    }

    @Value
    private static class TestResponse {

        String value;
    }
}
