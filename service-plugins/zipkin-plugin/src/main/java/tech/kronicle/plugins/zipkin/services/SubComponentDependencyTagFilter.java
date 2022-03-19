package tech.kronicle.plugins.zipkin.services;

import tech.kronicle.pluginapi.finders.models.GenericTag;
import tech.kronicle.plugins.zipkin.constants.TagKeys;
import tech.kronicle.plugins.zipkin.models.api.Span;
import tech.kronicle.utils.MapCollectors;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SubComponentDependencyTagFilter {

    private static final List<String> IDENTITY_TAG_KEYS = List.of(
            TagKeys.HTTP_PATH_TEMPLATE,
            TagKeys.EVENT_ORGANISATION_ID,
            TagKeys.EVENT_CHANNEL_ID,
            TagKeys.EVENT_TYPE,
            TagKeys.EVENT_VERSION);

    public List<GenericTag> filterTags(Span span) {
        return span.getTags().entrySet().stream()
                .filter(entry -> IDENTITY_TAG_KEYS.contains(entry.getKey()))
                .map(entry -> new GenericTag(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
