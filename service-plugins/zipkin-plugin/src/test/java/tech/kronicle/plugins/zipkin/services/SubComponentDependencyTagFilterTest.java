package tech.kronicle.plugins.zipkin.services;

import tech.kronicle.plugins.zipkin.constants.TagKeys;
import tech.kronicle.plugins.zipkin.models.api.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tech.kronicle.plugins.zipkin.services.SubComponentDependencyTagFilter;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SubComponentDependencyTagFilterTest {

    private SubComponentDependencyTagFilter underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new SubComponentDependencyTagFilter();
    }

    @ParameterizedTest
    @ValueSource(strings = { TagKeys.HTTP_PATH_TEMPLATE,
            TagKeys.EVENT_ORGANISATION_ID,
            TagKeys.EVENT_CHANNEL_ID,
            TagKeys.EVENT_TYPE,
            TagKeys.EVENT_VERSION })
    public void filterAndSortTagsShouldOnlyIncludeTagsThatArePartOfTheIdentityOfASubComponentDependency(String tagKey) {
        // Given
        Map<String, String> tags = Map.of("should.be.ignored", "test-value-1", tagKey, "test-value-2", "should.also.be.ignored", "test-value-3");
        Span span = new Span(null, null, null, null, null, null, null, null, null, null, null, null, tags);

        // When
        Map<String, String> returnValue = underTest.filterAndSortTags(span);

        // Then
        assertThat(returnValue).containsExactly(Map.entry(tagKey, "test-value-2"));
    }
}
