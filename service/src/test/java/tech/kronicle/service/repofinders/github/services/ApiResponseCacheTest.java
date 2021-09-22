package tech.kronicle.service.repofinders.github.services;

import lombok.Value;
import org.junit.jupiter.api.Test;
import tech.kronicle.service.repofinders.github.models.ApiResponseCacheEntry;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiResponseCacheTest {

    private static final String USERNAME = "test-user";
    private static final String URI = "http://example.com/test-uri";
    private static final String ETAG = "test-etag";
    private static final TestResponse TEST_RESPONSE = new TestResponse("test-response");
    private final ApiResponseCache underTest = new ApiResponseCache();

    @Test
    public void getEntryShouldReturnNullWhenEntryIsNotCached() {
        // When
        ApiResponseCacheEntry<TestResponse> returnValue = underTest.getEntry(USERNAME, URI);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getEntryShouldReturnExistingEntryWhenAlreadyCached() {
        // Given
        ApiResponseCacheEntry<TestResponse> entry = new ApiResponseCacheEntry<>(ETAG, TEST_RESPONSE);
        underTest.putEntry(USERNAME, URI, entry);

        // When
        ApiResponseCacheEntry<TestResponse> returnValue = underTest.getEntry(USERNAME, URI);

        // Then
        assertThat(returnValue).isSameAs(entry);
    }

    @Test
    public void getEntryShouldNotReturnExistingEntryWhenUsernameIsDifferent() {
        // Given
        ApiResponseCacheEntry<TestResponse> entry = new ApiResponseCacheEntry<>(ETAG, TEST_RESPONSE);
        underTest.putEntry(USERNAME, URI, entry);

        // When
        ApiResponseCacheEntry<TestResponse> returnValue = underTest.getEntry("different-user", URI);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getEntryShouldNotReturnExistingEntryWhenUriIsDifferent() {
        // Given
        ApiResponseCacheEntry<TestResponse> entry = new ApiResponseCacheEntry<>(ETAG, TEST_RESPONSE);
        underTest.putEntry(USERNAME, URI, entry);

        // When
        ApiResponseCacheEntry<TestResponse> returnValue = underTest.getEntry(USERNAME, "https://example.com/different-uri");

        // Then
        assertThat(returnValue).isNull();
    }

    @Value
    private static class TestResponse {

        String value;
    }
}
