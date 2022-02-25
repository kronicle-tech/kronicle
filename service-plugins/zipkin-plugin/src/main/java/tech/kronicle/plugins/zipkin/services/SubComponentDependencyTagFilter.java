package tech.kronicle.plugins.zipkin.services;

import org.springframework.stereotype.Service;
import tech.kronicle.plugins.zipkin.constants.TagKeys;
import tech.kronicle.plugins.zipkin.models.api.Span;
import tech.kronicle.pluginutils.utils.MapCollectors;

import java.util.List;
import java.util.Map;

@Service
public class SubComponentDependencyTagFilter {

    private static final List<String> IDENTITY_TAG_KEYS = List.of(
            TagKeys.HTTP_PATH_TEMPLATE,
            TagKeys.EVENT_ORGANISATION_ID,
            TagKeys.EVENT_CHANNEL_ID,
            TagKeys.EVENT_TYPE,
            TagKeys.EVENT_VERSION);

    public Map<String, String> filterAndSortTags(Span span) {
        return span.getTags().entrySet().stream()
                .filter(entry -> IDENTITY_TAG_KEYS.contains(entry.getKey()))
                .sorted(Map.Entry.comparingByKey())
                .collect(MapCollectors.toMap());
    }
}
