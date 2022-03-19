package tech.kronicle.plugins.zipkin.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tech.kronicle.pluginapi.finders.models.GenericTag;
import tech.kronicle.plugins.zipkin.constants.TagKeys;
import tech.kronicle.plugins.zipkin.models.api.Span;

import java.util.List;
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
    public void filterTagsShouldOnlyIncludeTagsThatArePartOfTheIdentityOfASubComponentDependency(String tagKey) {
        // Given
        Map<String, String> tags = Map.ofEntries(
                Map.entry("should.be.ignored", "test-value-1"),
                Map.entry(tagKey, "test-value-2"),
                Map.entry("should.also.be.ignored", "test-value-3")
        );
        Span span = Span.builder().tags(tags).build();

        // When
        List<GenericTag> returnValue = underTest.filterTags(span);

        // Then
        assertThat(returnValue).containsExactly(new GenericTag(tagKey, "test-value-2"));
    }
}
