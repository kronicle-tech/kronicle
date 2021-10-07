package tech.kronicle.service.repofinders.github.services;

import lombok.Value;
import org.junit.jupiter.api.Test;
import tech.kronicle.service.repofinders.github.config.GitHubRepoFinderPersonalAccessTokenConfig;
import tech.kronicle.service.repofinders.github.models.ApiResponseCacheEntry;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiResponseCacheTest {

    private static final GitHubRepoFinderPersonalAccessTokenConfig PERSONAL_ACCESS_TOKEN =
            new GitHubRepoFinderPersonalAccessTokenConfig("test-user", "test-personal-access-token");
    private static final GitHubRepoFinderPersonalAccessTokenConfig DIFFERENT_PERSONAL_ACCESS_TOKEN =
            new GitHubRepoFinderPersonalAccessTokenConfig("different-user", "different-personal-access-token");
    private static final String URI = "http://example.com/test-uri";
    private static final String ETAG = "test-etag";
    private static final TestResponse TEST_RESPONSE = new TestResponse("test-response");
    private final ApiResponseCache underTest = new ApiResponseCache();

    @Test
    public void getEntryShouldReturnNullWhenEntryIsNotCached() {
        // When
        ApiResponseCacheEntry<TestResponse> returnValue = underTest.getEntry(PERSONAL_ACCESS_TOKEN, URI);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getEntryShouldReturnExistingEntryWhenAlreadyCached() {
        // Given
        ApiResponseCacheEntry<TestResponse> entry = new ApiResponseCacheEntry<>(ETAG, TEST_RESPONSE);
        underTest.putEntry(PERSONAL_ACCESS_TOKEN, URI, entry);

        // When
        ApiResponseCacheEntry<TestResponse> returnValue = underTest.getEntry(PERSONAL_ACCESS_TOKEN, URI);

        // Then
        assertThat(returnValue).isSameAs(entry);
    }

    @Test
    public void getEntryShouldNotReturnExistingEntryWhenUsernameIsDifferent() {
        // Given
        ApiResponseCacheEntry<TestResponse> entry = new ApiResponseCacheEntry<>(ETAG, TEST_RESPONSE);
        underTest.putEntry(PERSONAL_ACCESS_TOKEN, URI, entry);

        // When
        ApiResponseCacheEntry<TestResponse> returnValue = underTest.getEntry(DIFFERENT_PERSONAL_ACCESS_TOKEN, URI);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getEntryShouldNotReturnExistingEntryWhenUriIsDifferent() {
        // Given
        ApiResponseCacheEntry<TestResponse> entry = new ApiResponseCacheEntry<>(ETAG, TEST_RESPONSE);
        underTest.putEntry(PERSONAL_ACCESS_TOKEN, URI, entry);

        // When
        ApiResponseCacheEntry<TestResponse> returnValue = underTest.getEntry(PERSONAL_ACCESS_TOKEN, "https://example.com/different-uri");

        // Then
        assertThat(returnValue).isNull();
    }

    @Value
    private static class TestResponse {

        String value;
    }
}
