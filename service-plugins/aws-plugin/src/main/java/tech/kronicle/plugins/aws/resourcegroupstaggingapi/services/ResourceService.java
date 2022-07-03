package tech.kronicle.plugins.aws.resourcegroupstaggingapi.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.pluginapi.finders.models.ComponentsAndDiagrams;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ComponentAndConnection;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Diagram;
import tech.kronicle.sdk.models.DiagramConnection;

import javax.inject.Inject;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toUnmodifiableList;
import static tech.kronicle.plugins.aws.utils.ProfileUtils.*;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ResourceService {

    private final ResourceFetcher fetcher;
    private final ResourceMapper mapper;
    private final AwsConfig config;

    public ComponentsAndDiagrams getComponentsAndDiagrams() {
        List<ComponentsAndDiagrams> componentsAndDiagramsList = groupComponentsAndConnectionsByEnvironmentId(
                getComponentAndConnectionEntries()
        )
                .stream()
                .map(entry -> new ComponentsAndDiagrams(
                        getComponents(entry),
                        createDiagram(entry.getKey(), getConnections(entry))
                ))
                .collect(toUnmodifiableList());
        return flattenComponentsAndDiagrams(componentsAndDiagramsList);
    }

    private List<Map.Entry<String, List<ComponentAndConnection>>> groupComponentsAndConnectionsByEnvironmentId(
            List<Map.Entry<AwsProfileAndRegion,
            List<ComponentAndConnection>>> componentAndConnectionEntries
    ) {
        return componentAndConnectionEntries.stream()
                .map(entry -> Map.entry(
                        entry.getKey().getProfile().getEnvironmentId(),
                        entry.getValue()
                ))
                .collect(groupingBy(Map.Entry::getKey))
                .entrySet().stream()
                .map(entry -> Map.entry(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(Map.Entry::getValue)
                                .flatMap(Collection::stream)
                                .collect(toUnmodifiableList())
                ))
                .collect(toUnmodifiableList());
    }

    private List<Component> getComponents(Map.Entry<String, List<ComponentAndConnection>> entry) {
        return entry.getValue()
                .stream()
                .map(ComponentAndConnection::getComponent)
                .collect(toUnmodifiableList());
    }

    private List<DiagramConnection> getConnections(Map.Entry<String, List<ComponentAndConnection>> entry) {
        return entry.getValue()
                .stream()
                .map(ComponentAndConnection::getConnection)
                .filter(Objects::nonNull)
                .collect(toUnmodifiableList());
    }

    private List<Diagram> createDiagram(String environmentId, List<DiagramConnection> connections) {
        if (connections.isEmpty()) {
            return List.of();
        }
        return List.of(
                Diagram.builder()
                        .id("aws-resources-" + environmentId)
                        .name("AWS Resources - " + environmentId)
                        .description("This diagram shows AWS resources that have associated components.  Any AWS " +
                                "resources that are not associated with a component will not appear in the diagram")
                        .discovered(true)
                        .connections(connections)
                        .build()
        );
    }

    private ComponentsAndDiagrams flattenComponentsAndDiagrams(List<ComponentsAndDiagrams> componentsAndDiagramsList) {
        return new ComponentsAndDiagrams(
                flattenItems(componentsAndDiagramsList, ComponentsAndDiagrams::getComponents),
                flattenItems(componentsAndDiagramsList, ComponentsAndDiagrams::getDiagrams)
        );
    }

    private <T> List<T> flattenItems(
            List<ComponentsAndDiagrams> componentsAndDiagramsList,
            Function<ComponentsAndDiagrams, List<T>> getItems
    ) {
        return componentsAndDiagramsList.stream()
                .map(getItems)
                .flatMap(Collection::stream)
                .collect(toUnmodifiableList());
    }

    private List<Map.Entry<AwsProfileAndRegion, List<ComponentAndConnection>>> getComponentAndConnectionEntries() {
        return processProfilesToMapEntryList(
                config.getProfiles(),
                profileAndRegion -> mapper.mapResourcesToComponentsAndConnections(
                        profileAndRegion.getProfile().getEnvironmentId(),
                        fetcher.getResources(profileAndRegion)
                )
        );
    }
}
