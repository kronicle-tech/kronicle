package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.kronicle.common.CaseUtils;
import tech.kronicle.sdk.models.*;
import tech.kronicle.service.exceptions.ValidationException;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableSet;

/**
 * This class takes a merged/combined `ComponentMetadata` object and uses it to load maps of areas, teams and components. It performs various bits of
 * validation on each area, team and component.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ComponentMetadataLoader {

    private final ValidatorService validatorService;

    public Output loadComponentMetadata(ComponentMetadata componentMetadata) {
        ConcurrentHashMap<String, ComponentType> componentTypes = loadMapItems("component type", componentMetadata,
                ComponentMetadata::getComponentTypes, validatorService::validate);
        ConcurrentHashMap<String, Platform> platforms = loadMapItems(
                "platform",
                componentMetadata,
                ComponentMetadata::getPlatforms,
                validatorService::validate
        );
        ConcurrentHashMap<String, Area> areas = loadMapItems(
                "area",
                componentMetadata,
                ComponentMetadata::getAreas,
                validatorService::validate
        );
        ConcurrentHashMap<String, Team> teams = loadMapItems(
                "team",
                componentMetadata,
                ComponentMetadata::getTeams,
                validateTeam(areas)
        );
        Set<String> componentIds = getComponentIds(componentMetadata.getComponents());
        ConcurrentHashMap<String, Component> components = loadMapItems(
                "component",
                componentMetadata,
                ComponentMetadata::getComponents,
                validateComponent(componentTypes, platforms, teams, componentIds)
        );
        ConcurrentHashMap<String, Diagram> diagrams = loadMapItems(
                "diagram",
                componentMetadata,
                ComponentMetadata::getDiagrams,
                validateDiagram(componentIds)
        );
        return new Output(areas, teams, components, diagrams);
    }

    private <T extends ObjectWithId> ConcurrentHashMap<String, T> loadMapItems(String itemType, ComponentMetadata componentMetadataItem,
            Function<ComponentMetadata, List<T>> itemsGetter, Consumer<T> itemValidator) {
        ConcurrentHashMap<String, T> itemMap = new ConcurrentHashMap<>();
        List<T> items = itemsGetter.apply(componentMetadataItem);
        if (nonNull(items)) {
            items.forEach(item -> loadMapItem(itemType, item, itemMap, itemValidator));
        }
        log.info("Loaded {} {}s", itemMap.size(), itemType);
        return itemMap;
    }

    private <T extends ObjectWithId> void loadMapItem(String itemType, T item, ConcurrentHashMap<String, T> itemMap, Consumer<T> itemValidator) {
        try {
            itemValidator.accept(item);
        } catch (ValidationException e) {
            log.warn("{} id {} failed validation and will be skipped", CaseUtils.toTitleCase(itemType), item.getId(), e);
            return;
        }

        if (itemMap.containsKey(item.getId())) {
            log.warn("{} id {} is defined at least twice and will be skipped this time", CaseUtils.toTitleCase(itemType), item.getId());
            return;
        }

        itemMap.put(item.getId(), item);
    }

    private Consumer<Team> validateTeam(ConcurrentHashMap<String, Area> areas) {
        return team -> {
            validatorService.validate(team);
            if (nonNull(team.getAreaId()) && !areas.containsKey(team.getAreaId())) {
                log.warn("Cannot find area {} for team {}", team.getAreaId(), team.getId());
            }
        };
    }

    private Consumer<Component> validateComponent(ConcurrentHashMap<String, ComponentType> componentTypes, ConcurrentHashMap<String, Platform> platforms,
                                                  ConcurrentHashMap<String, Team> teams, Set<String> componentIds) {
        return component -> {
            validatorService.validate(component);
            if (!componentTypes.containsKey(component.getType())) {
                log.warn("Cannot find component type {} for component {}", component.getType(), component.getId());
            }
            component.getTeams().forEach(componentTeam -> {
                if (!teams.containsKey(componentTeam.getTeamId())) {
                    log.warn("Cannot find team {} for component {}", componentTeam.getTeamId(), component.getId());
                }
            });
            if (nonNull(component.getPlatformId()) && !platforms.containsKey(component.getPlatformId())) {
                log.warn("Cannot find platform {} for component {}", component.getPlatformId(), component.getId());
            }
            component.getConnections().forEach(connection -> {
                if (!componentIds.contains(connection.getTargetComponentId())) {
                    log.warn("Cannot find target component {} for connection of component {}", connection.getTargetComponentId(), component.getId());
                }
            });
        };
    }

    private Consumer<Diagram> validateDiagram(Set<String> componentIds) {
        return diagram -> {
            validatorService.validate(diagram);
            diagram.getConnections().forEach(connection -> {
                validationDiagramConnectionNode(
                        diagram,
                        "source",
                        connection.getSourceComponentId(),
                        componentIds
                );
                validationDiagramConnectionNode(
                        diagram,
                        "target",
                        connection.getTargetComponentId(),
                        componentIds
                );
            });
        };
    }

    private void validationDiagramConnectionNode(
            Diagram diagram, String nodeType, String connectionComponentId, Set<String> componentIds
    ) {
        if (!componentIds.contains(connectionComponentId)) {
            log.warn("Cannot find {} component {} for connection of diagram {}", nodeType, connectionComponentId, diagram.getId());
        }
    }

    private Set<String> getComponentIds(List<Component> components) {
        return components.stream()
                .map(Component::getId)
                .collect(toUnmodifiableSet());
    }

    @Value
    public static class Output {
        
        ConcurrentHashMap<String, Area> areas;
        ConcurrentHashMap<String, Team> teams;
        ConcurrentHashMap<String, Component> components;
        ConcurrentHashMap<String, Diagram> diagrams;
    }
}
