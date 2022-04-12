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
public class ResourceIdsByProfileAndRegionAndComponent {

    private final List<Map.Entry<AwsProfileAndRegion, Map<String, List<String>>>> resourceIdsByProfileAndRegionAndComponent;

    public List<String> getResourceIds(
            AwsProfileAndRegion profileAndRegion,
            Component component
    ) {
        Map<String, List<String>> resourceIdsByComponent = resourceIdsByProfileAndRegionAndComponent.stream()
                .filter(it -> Objects.equals(it.getKey(), profileAndRegion))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
        if (isNull(resourceIdsByComponent)) {
            return List.of();
        }
        List<String> resourceIds = resourceIdsByComponent.get(component.getId());
        if (nonNull(resourceIds)) {
            return resourceIds;
        }
        return component.getAliases().stream()
                .map(Alias::getId)
                .map(resourceIdsByComponent::get)
                .filter(Objects::nonNull)
                .findFirst().orElse(List.of());
    }
}
