package tech.kronicle.pluginapi.finders.models;

import lombok.Builder;
import lombok.Value;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@Builder
public class GenericSpan {

    String id;
    String parentId;
    String sourceName;
    String name;
    List<GenericTag> subComponentTags;
    Long timestamp;
    Long duration;

    public GenericSpan(String id, String parentId, String sourceName, String name, List<GenericTag> subComponentTags, Long timestamp, Long duration) {
        this.id = id;
        this.parentId = parentId;
        this.sourceName = sourceName;
        this.name = name;
        this.subComponentTags = createUnmodifiableListSortedByTagKey(subComponentTags);
        this.timestamp = timestamp;
        this.duration = duration;
    }

    private List<GenericTag> createUnmodifiableListSortedByTagKey(List<GenericTag> list) {
        if (isNull(list)) {
            return List.of();
        }

        return createUnmodifiableList(list.stream()
                .sorted(Comparator.comparing(GenericTag::getKey))
                .collect(Collectors.toList()));
    }
}
