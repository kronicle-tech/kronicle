package tech.kronicle.service.services;

import org.springframework.stereotype.Service;
import tech.kronicle.sdk.models.Area;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentTeam;
import tech.kronicle.sdk.models.ComponentTeamType;
import tech.kronicle.sdk.models.Team;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class assembles teams and areas. It adds associated components to teams. It add associated teams and components to areas.
 *
 * It also creates sorted, unmodifiable lists of components.
 */
@Service
public class ComponentMetadataAssembler {

    public List<Component> toSortedUnmodifiableComponentList(Stream<Component> componentStream) {
        return componentStream.sorted(Comparator.comparing(Component::getName))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Team> toSortedUnmodifiableTeamList(Stream<Team> teamStream, Map<String, Component> components) {
        return toSortedUnmodifiableTeamList(teamStream.map(team -> addNestedItemsToTeam(team, components)));
    }

    public Team addNestedItemsToTeam(Team team, Map<String, Component> components) {
        return team.withComponents(getComponentsByTeam(components, team));
    }

    public List<Area> toSortedUnmodifiableAreaList(Stream<Area> areaStream, Map<String, Team> teams,
            Map<String, Component> components) {
        return areaStream
                .map(area -> addNestedItemsToArea(area, teams, components))
                .sorted(Comparator.comparing(Area::getName))
                .collect(Collectors.toUnmodifiableList());
    }

    public Area addNestedItemsToArea(Area area, Map<String, Team> teams, Map<String, Component> components) {
        List<Team> areaTeams = getTeamsByArea(teams, area);
        return area.withTeams(areaTeams)
                .withComponents(getComponentsByTeams(components, areaTeams));
    }

    private List<Team> getTeamsByArea(Map<String, Team> teams, Area area) {
        return toSortedUnmodifiableTeamList(teams.values().stream()
                .filter(team -> Objects.equals(team.getAreaId(), area.getId())));
    }

    private List<Component> getComponentsByTeam(Map<String, Component> components, Team team) {
        return getComponentsByPredicate(components, componentMatchesTeam(team));
    }

    private List<Component> getComponentsByTeams(Map<String, Component> components, List<Team> teams) {
        return getComponentsByPredicate(components, componentMatchesTeams(teams));
    }

    private List<Component> getComponentsByPredicate(Map<String, Component> components, Predicate<Component> componentPredicate) {
        return toSortedUnmodifiableComponentList(components.values().stream().filter(componentPredicate));
    }

    private Predicate<Component> componentMatchesTeam(Team team) {
        String teamId = team.getId();
        return componentMatchesTeamIdPredicate(teamId::equals);
    }

    private Predicate<Component> componentMatchesTeams(List<Team> teams) {
        Set<String> teamIds = getTeamIds(teams);
        return componentMatchesTeamIdPredicate(teamIds::contains);
    }

    private Predicate<Component> componentMatchesTeamIdPredicate(Predicate<String> teamIdPredicate) {
        return component -> component.getTeams().stream()
                .filter(this::componentTeamIsNotPreviousTeam)
                .map(ComponentTeam::getTeamId)
                .anyMatch(teamIdPredicate);
    }

    private boolean componentTeamIsNotPreviousTeam(ComponentTeam componentTeam) {
        return !Objects.equals(componentTeam.getType(), ComponentTeamType.PREVIOUS);
    }

    private Set<String> getTeamIds(List<Team> areaTeams) {
        return areaTeams.stream()
                .map(Team::getId)
                .collect(Collectors.toSet());
    }

    private List<Team> toSortedUnmodifiableTeamList(Stream<Team> teamStream) {
        return teamStream.sorted(Comparator.comparing(Team::getName))
                .collect(Collectors.toUnmodifiableList());
    }
}
