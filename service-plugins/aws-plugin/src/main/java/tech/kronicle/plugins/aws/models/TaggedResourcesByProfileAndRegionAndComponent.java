package tech.kronicle.plugins.aws.models;

import lombok.RequiredArgsConstructor;
import tech.kronicle.sdk.models.Alias;
import tech.kronicle.sdk.models.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
public class TaggedResourcesByProfileAndRegionAndComponent {

    private final List<Map.Entry<AwsProfileAndRegion, Map<String, List<TaggedResource>>>> taggedResourcesByProfileAndRegionAndComponent;

    public List<TaggedResource> getTaggedResources(
            AwsProfileAndRegion profileAndRegion,
            Component component
    ) {
        Map<String, List<TaggedResource>> taggedResourcesByComponent = taggedResourcesByProfileAndRegionAndComponent.stream()
                .filter(it -> Objects.equals(it.getKey(), profileAndRegion))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
        if (isNull(taggedResourcesByComponent)) {
            return List.of();
        }
        List<TaggedResource> taggedResources = taggedResourcesByComponent.get(component.getId());
        if (nonNull(taggedResources)) {
            return taggedResources;
        }
        return component.getAliases().stream()
                .map(Alias::getId)
                .map(taggedResourcesByComponent::get)
                .filter(Objects::nonNull)
                .findFirst().orElse(List.of());
    }
}
