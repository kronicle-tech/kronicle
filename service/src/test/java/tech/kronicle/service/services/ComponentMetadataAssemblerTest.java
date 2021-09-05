package tech.kronicle.service.services;

import tech.kronicle.sdk.models.Area;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentTeam;
import tech.kronicle.sdk.models.ComponentTeamType;
import tech.kronicle.sdk.models.ObjectWithId;
import tech.kronicle.sdk.models.Team;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ComponentMetadataAssemblerTest {

    private final ComponentMetadataAssembler underTest = new ComponentMetadataAssembler();

    @Test
    public void toSortedUnmodifiableComponentListShouldSortComponentsByName() {
        // Given
        Component componentA = Component.builder().name("Component Name A").build();
        Component componentB = Component.builder().name("Component Name B").build();
        Component componentC = Component.builder().name("Component Name C").build();
        Stream<Component> componentStream = Stream.of(componentC, componentA, componentB);

        // When
        List<Component> returnValue = underTest.toSortedUnmodifiableComponentList(componentStream);

        // Then
        assertThat(returnValue).containsExactly(componentA, componentB, componentC);
    }

    @Test
    public void toSortedUnmodifiableComponentListShouldCreateAnUnmodifiableList() {
        // Given
        Component component1 = Component.builder().name("Component Name 1").build();
        Stream<Component> componentStream = Stream.of(component1);

        // When
        Throwable thrown = catchThrowable(() -> underTest.toSortedUnmodifiableComponentList(componentStream).add(Component.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void toSortedUnmodifiableTeamListShouldSortTeamsByName() {
        // Given
        Team teamA = Team.builder().id("team-id-3").name("Team Name A").build();
        Team teamB = Team.builder().id("team-id-1").name("Team Name B").build();
        Team teamC = Team.builder().id("team-id-2").name("Team Name C").build();
        Stream<Team> teamStream = Stream.of(teamC, teamA, teamB);
        Map<String, Component> components = Map.of();

        // When
        List<Team> returnValue = underTest.toSortedUnmodifiableTeamList(teamStream, components);

        // Then
        assertThat(returnValue).containsExactly(teamA, teamB, teamC);
    }

    @Test
    public void toSortedUnmodifiableTeamListShouldCreateAnUnmodifiableList() {
        // Given
        Team team1 = Team.builder().id("team-id-1").build();
        Stream<Team> teamStream = Stream.of(team1);
        Map<String, Component> components = Map.of();

        // When
        Throwable thrown = catchThrowable(() -> underTest.toSortedUnmodifiableTeamList(teamStream, components).add(Team.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void toSortedUnmodifiableTeamListShouldAddComponentsToTeams() {
        // Given
        Team team1 = Team.builder().id("team-id-1").name("Team Name 1").build();
        Team team2 = Team.builder().id("team-id-2").name("Team Name 2").build();
        Team team3 = Team.builder().id("team-id-3").name("Team Name 3").build();
        Stream<Team> teamStream = Stream.of(team1, team2, team3);
        Component component1 = Component.builder()
                .id("component-id-1")
                .name("Component Name 1")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team1.getId()).build(),
                        ComponentTeam.builder().teamId(team2.getId()).build()))
                .build();
        Component component2 = Component.builder()
                .id("component-id-2")
                .name("Component Name 2")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team1.getId()).build()))
                .build();
        Component component3 = Component.builder()
                .id("component-id-3")
                .name("Component Name 3")
                .build();
        Map<String, Component> components = Map.ofEntries(
                createMapEntry(component1),
                createMapEntry(component2),
                createMapEntry(component3));

        // When
        List<Team> returnValue = underTest.toSortedUnmodifiableTeamList(teamStream, components);

        // Then
        assertThat(returnValue).hasSize(3);
        Team team;
        team = returnValue.get(0);
        assertThat(team.getName()).isEqualTo("Team Name 1");
        assertThat(team.getComponents()).containsExactly(component1, component2);
        team = returnValue.get(1);
        assertThat(team.getName()).isEqualTo("Team Name 2");
        assertThat(team.getComponents()).containsExactly(component1);
        team = returnValue.get(2);
        assertThat(team.getName()).isEqualTo("Team Name 3");
        assertThat(team.getComponents()).isEmpty();
    }

    @Test
    public void addNestedItemsToTeamShouldAddComponentsToATeam() {
        // Given
        Team team1 = Team.builder().id("team-id-1").name("Team Name 1").build();
        Component component1 = Component.builder()
                .id("component-id-1")
                .name("Component Name 1")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team1.getId()).build()))
                .build();
        Component component2 = Component.builder()
                .id("component-id-2")
                .name("Component Name 2")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team1.getId()).build()))
                .build();
        Component component3 = Component.builder()
                .id("component-id-3")
                .name("Component Name 3")
                .teams(List.of(
                        ComponentTeam.builder().teamId("team-id-2").build()))
                .build();
        Component component4 = Component.builder()
                .id("component-id-4")
                .name("Component Name 4")
                .build();
        Map<String, Component> components = Map.ofEntries(
                createMapEntry(component1),
                createMapEntry(component2),
                createMapEntry(component3),
                createMapEntry(component4));

        // When
        Team returnValue = underTest.addNestedItemsToTeam(team1, components);

        // Then
        assertThat(returnValue.getName()).isEqualTo("Team Name 1");
        assertThat(returnValue.getComponents()).containsExactly(component1, component2);
    }

    @ParameterizedTest
    @MethodSource("provideTeamMethods")
    public void teamMethodsShouldAddComponentsToATeamSortedByName(TeamMethod teamMethod) {
        // Given
        Team team = Team.builder().id("team-id-1").name("Team Name 1").build();
        Component componentA = Component.builder()
                .id("component-id-3")
                .name("Component Name A")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team.getId()).build()))
                .build();
        Component componentB = Component.builder()
                .id("component-id-1")
                .name("Component Name B")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team.getId()).build()))
                .build();
        Component componentC = Component.builder()
                .id("component-id-2")
                .name("Component Name C")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team.getId()).build()))
                .build();
        // LinkedHashMap is used to ensure the maps entries are always NOT sorted by name
        Map<String, Component> components = new LinkedHashMap<>();
        addMapEntry(components, componentC);
        addMapEntry(components, componentA);
        addMapEntry(components, componentB);

        // When
        Team returnValue = teamMethod.call(underTest, team, components);

        // Then
        assertThat(returnValue.getName()).isEqualTo("Team Name 1");
        // Assert that components have been sorted by name
        assertThat(returnValue.getComponents()).containsExactly(componentA, componentB, componentC);
    }

    @ParameterizedTest
    @MethodSource("provideTeamMethods")
    public void teamMethodsShouldMakeComponentListForATeamAnUnmodifiableList(TeamMethod teamMethod) {
        // Given
        Team team = Team.builder().id("team-id-1").name("Team Name 1").build();
        Component component1 = Component.builder()
                .id("component-id-1")
                .name("Component Name 1")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team.getId()).build()))
                .build();
        Map<String, Component> components = Map.ofEntries(
                createMapEntry(component1));

        // When
        Throwable thrown = catchThrowable(() -> teamMethod.call(underTest, team, components).getComponents().add(Component.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("provideTeamMethods")
    public void teamMethodsShouldIgnoreAPreviousTeamForAComponent(TeamMethod teamMethod) {
        // Given
        Team team = Team.builder().id("team-id-1").name("Team Name 1").build();
        Component component1 = Component.builder()
                .id("component-id-1")
                .name("Component Name 1")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team.getId()).type(ComponentTeamType.PREVIOUS).build()))
                .build();
        Component component2 = Component.builder()
                .id("component-id-2")
                .name("Component Name 2")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team.getId()).build()))
                .build();
        Map<String, Component> components = Map.ofEntries(
                createMapEntry(component1),
                createMapEntry(component2));

        // When
        Team returnValue = teamMethod.call(underTest, team, components);

        // Then
        assertThat(returnValue.getName()).isEqualTo("Team Name 1");
        assertThat(returnValue.getComponents()).containsExactly(component2);
    }

    @ParameterizedTest
    @MethodSource("provideTeamMethods")
    public void teamMethodsShouldNotIgnoreAPrimaryTeamForAComponent(TeamMethod teamMethod) {
        // Given
        Team team = Team.builder().id("team-id-1").name("Team Name 1").build();
        Component component1 = Component.builder()
                .id("component-id-1")
                .name("Component Name 1")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team.getId()).type(ComponentTeamType.PRIMARY).build()))
                .build();
        Map<String, Component> components = Map.ofEntries(createMapEntry(component1));

        // When
        Team returnValue = teamMethod.call(underTest, team, components);

        // Then
        assertThat(returnValue.getName()).isEqualTo("Team Name 1");
        assertThat(returnValue.getComponents()).containsExactly(component1);
    }

    @Test
    public void toSortedUnmodifiableAreaListShouldSortAreasByName() {
        // Given
        Area areaA = Area.builder().id("area-id-3").name("Area Name A").build();
        Area areaB = Area.builder().id("area-id-1").name("Area Name B").build();
        Area areaC = Area.builder().id("area-id-2").name("Area Name C").build();
        Stream<Area> areaStream = Stream.of(areaC, areaA, areaB);
        Map<String, Team> teams = Map.of();
        Map<String, Component> components = Map.of();

        // When
        List<Area> returnValue = underTest.toSortedUnmodifiableAreaList(areaStream, teams, components);

        // Then
        assertThat(returnValue).containsExactly(areaA, areaB, areaC);
    }

    @Test
    public void toSortedUnmodifiableAreaListShouldCreateAnUnmodifiableList() {
        // Given
        Area area1 = Area.builder().id("area-id-1").build();
        Stream<Area> areaStream = Stream.of(area1);
        Map<String, Team> teams = Map.of();
        Map<String, Component> components = Map.of();

        // When
        Throwable thrown = catchThrowable(() -> underTest.toSortedUnmodifiableAreaList(areaStream, teams, components).add(Area.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void toSortedUnmodifiableAreaListShouldAddTeamsAndComponentsToAreas() {
        // Given
        Area area1 = Area.builder().id("area-id-1").name("Area Name 1").build();
        Area area2 = Area.builder().id("area-id-2").name("Area Name 2").build();
        Area area3 = Area.builder().id("area-id-3").name("Area Name 3").build();
        Stream<Area> areaStream = Stream.of(area1, area2, area3);
        Team team1 = Team.builder().id("team-id-1").name("Team Name 1").areaId(area1.getId()).build();
        Team team2 = Team.builder().id("team-id-2").name("Team Name 2").areaId(area1.getId()).build();
        Team team3 = Team.builder().id("team-id-3").name("Team Name 3").areaId(area2.getId()).build();
        Team team4 = Team.builder().id("team-id-4").name("Team Name 4").build();
        Map<String, Team> teams = Map.ofEntries(
                createMapEntry(team1),
                createMapEntry(team2),
                createMapEntry(team3),
                createMapEntry(team4));
        Component component1 = Component.builder()
                .id("component-id-1")
                .name("Component Name 1")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team1.getId()).build(),
                        ComponentTeam.builder().teamId(team2.getId()).build(),
                        ComponentTeam.builder().teamId(team3.getId()).build()))
                .build();
        Component component2 = Component.builder()
                .id("component-id-2")
                .name("Component Name 2")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team1.getId()).build(),
                        ComponentTeam.builder().teamId(team2.getId()).build()))
                .build();
        Component component3 = Component.builder()
                .id("component-id-3")
                .name("Component Name 3")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team1.getId()).build()))
                .build();
        Component component4 = Component.builder()
                .id("component-id-4")
                .name("Component Name 4")
                .build();
        Map<String, Component> components = Map.ofEntries(
                createMapEntry(component1),
                createMapEntry(component2),
                createMapEntry(component3),
                createMapEntry(component4));

        // When
        List<Area> returnValue = underTest.toSortedUnmodifiableAreaList(areaStream, teams, components);

        // Then
        assertThat(returnValue).hasSize(3);
        Area area;
        area = returnValue.get(0);
        assertThat(area.getName()).isEqualTo("Area Name 1");
        assertThat(area.getTeams()).containsExactly(team1, team2);
        assertThat(area.getComponents()).containsExactly(component1, component2, component3);
        area = returnValue.get(1);
        assertThat(area.getName()).isEqualTo("Area Name 2");
        assertThat(area.getTeams()).containsExactly(team3);
        assertThat(area.getComponents()).containsExactly(component1);
        area = returnValue.get(2);
        assertThat(area.getName()).isEqualTo("Area Name 3");
        assertThat(area.getTeams()).isEmpty();
        assertThat(area.getComponents()).isEmpty();
    }

    @Test
    public void addNestedItemsToAreaShouldAddTeamsAndComponentsToAnArea() {
        // Given
        Area area1 = Area.builder().id("area-id-1").name("Area Name 1").build();
        Team team1 = Team.builder().id("team-id-1").name("Team Name 1").areaId(area1.getId()).build();
        Team team2 = Team.builder().id("team-id-2").name("Team Name 2").areaId(area1.getId()).build();
        Team team3 = Team.builder().id("team-id-3").name("Team Name 3").build();
        Map<String, Team> teams = Map.ofEntries(
                createMapEntry(team1),
                createMapEntry(team2),
                createMapEntry(team3));
        Component component1 = Component.builder()
                .id("component-id-1")
                .name("Component Name 1")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team1.getId()).build()))
                .build();
        Component component2 = Component.builder()
                .id("component-id-2")
                .name("Component Name 2")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team1.getId()).build()))
                .build();
        Component component3 = Component.builder()
                .id("component-id-3")
                .name("Component Name 3")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team3.getId()).build()))
                .build();
        Component component4 = Component.builder()
                .id("component-id-4")
                .name("Component Name 4")
                .build();
        Map<String, Component> components = Map.ofEntries(
                createMapEntry(component1),
                createMapEntry(component2),
                createMapEntry(component3),
                createMapEntry(component4));

        // When
        Area returnValue = underTest.addNestedItemsToArea(area1, teams, components);

        // Then
        assertThat(returnValue.getName()).isEqualTo("Area Name 1");
        assertThat(returnValue.getTeams()).containsExactly(team1, team2);
        assertThat(returnValue.getComponents()).containsExactly(component1, component2);
    }

    @ParameterizedTest
    @MethodSource("provideAreaMethods")
    public void areaMethodsShouldAddComponentsToAnAreaSortedByName(AreaMethod areaMethod) {
        // Given
        Area area = Area.builder().id("area-id-1").name("Area Name 1").build();
        Team team = Team.builder().id("team-id-1").name("Team Name 1").areaId(area.getId()).build();
        Map<String, Team> teams = Map.ofEntries(
                createMapEntry(team));
        Component componentA = Component.builder()
                .id("component-id-3")
                .name("Component Name A")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team.getId()).build()))
                .build();
        Component componentB = Component.builder()
                .id("component-id-1")
                .name("Component Name B")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team.getId()).build()))
                .build();
        Component componentC = Component.builder()
                .id("component-id-2")
                .name("Component Name C")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team.getId()).build()))
                .build();
        // LinkedHashMap is used to ensure the maps entries are always NOT sorted by name
        Map<String, Component> components = new LinkedHashMap<>();
        addMapEntry(components, componentC);
        addMapEntry(components, componentA);
        addMapEntry(components, componentB);

        // When
        Area returnValue = areaMethod.call(underTest, area, teams, components);

        // Then
        assertThat(returnValue.getName()).isEqualTo("Area Name 1");
        assertThat(returnValue.getTeams()).containsExactly(team);
        // Assert that components have been sorted by name
        assertThat(returnValue.getComponents()).containsExactly(componentA, componentB, componentC);
    }

    @ParameterizedTest
    @MethodSource("provideAreaMethods")
    public void areaMethodsShouldAddTeamsToAnAreaSortedByName(AreaMethod areaMethod) {
        // Given
        Area area = Area.builder().id("area-id-1").name("Area Name 1").build();
        Team teamA = Team.builder()
                .id("team-id-3")
                .name("Team Name A")
                .areaId(area.getId())
                .build();
        Team teamB = Team.builder()
                .id("team-id-1")
                .name("Team Name B")
                .areaId(area.getId())
                .build();
        Team teamC = Team.builder()
                .id("team-id-2")
                .name("Team Name C")
                .areaId(area.getId())
                .build();
        // LinkedHashMap is used to ensure the maps entries are always NOT sorted by name
        Map<String, Team> teams = new LinkedHashMap<>();
        addMapEntry(teams, teamC);
        addMapEntry(teams, teamA);
        addMapEntry(teams, teamB);
        Map<String, Component> components = Map.of();

        // When
        Area returnValue = areaMethod.call(underTest, area, teams, components);

        // Then
        assertThat(returnValue.getName()).isEqualTo("Area Name 1");
        // Assert that teams have been sorted by name
        assertThat(returnValue.getTeams()).containsExactly(teamA, teamB, teamC);
        assertThat(returnValue.getComponents()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("provideAreaMethods")
    public void areaMethodsShouldMakeComponentListForAnAreaAnUnmodifiableList(AreaMethod areaMethod) {
        // Given
        Area area = Area.builder().id("area-id-1").name("Area Name 1").build();
        Team team = Team.builder().id("team-id-1").name("Team Name 1").areaId(area.getId()).build();
        Map<String, Team> teams = Map.ofEntries(
                createMapEntry(team));
        Component component1 = Component.builder()
                .id("component-id-1")
                .name("Component Name 1")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team.getId()).build()))
                .build();
        Map<String, Component> components = Map.ofEntries(
                createMapEntry(component1));

        // When
        Throwable thrown = catchThrowable(() -> areaMethod.call(underTest, area, teams, components).getComponents().add(Component.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("provideAreaMethods")
    public void areaMethodsShouldMakeTeamListForAnAreaAnUnmodifiableList(AreaMethod areaMethod) {
        // Given
        Area area = Area.builder().id("area-id-1").name("Area Name 1").build();
        Team team = Team.builder().id("team-id-1").name("Team Name 1").areaId(area.getId()).build();
        Map<String, Team> teams = Map.ofEntries(
                createMapEntry(team));
        Map<String, Component> components = Map.of();

        // When
        Throwable thrown = catchThrowable(() -> areaMethod.call(underTest, area, teams, components).getTeams().add(Team.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("provideAreaMethods")
    public void areaMethodsShouldIgnoreAPreviousTeamForAComponent(AreaMethod areaMethod) {
        // Given
        Area area = Area.builder().id("area-id-1").name("Area Name 1").build();
        Team team = Team.builder().id("team-id-1").name("Team Name 1").areaId(area.getId()).build();
        Map<String, Team> teams = Map.ofEntries(
                createMapEntry(team));
        Component component1 = Component.builder()
                .id("component-id-1")
                .name("Component Name 1")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team.getId()).type(ComponentTeamType.PREVIOUS).build()))
                .build();
        Component component2 = Component.builder()
                .id("component-id-2")
                .name("Component Name 2")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team.getId()).build()))
                .build();
        Map<String, Component> components = Map.ofEntries(
                createMapEntry(component1),
                createMapEntry(component2));

        // When
        Area returnValue = areaMethod.call(underTest, area, teams, components);

        // Then
        assertThat(returnValue.getName()).isEqualTo("Area Name 1");
        assertThat(returnValue.getTeams()).containsExactly(team);
        assertThat(returnValue.getComponents()).containsExactly(component2);
    }

    @ParameterizedTest
    @MethodSource("provideAreaMethods")
    public void areaMethodsShouldNotIgnoreAPrimaryTeamForAComponent(AreaMethod areaMethod) {
        // Given
        Area area = Area.builder().id("area-id-1").name("Area Name 1").build();
        Team team = Team.builder().id("team-id-1").name("Team Name 1").areaId(area.getId()).build();
        Map<String, Team> teams = Map.ofEntries(
                createMapEntry(team));
        Component component1 = Component.builder()
                .id("component-id-1")
                .name("Component Name 1")
                .teams(List.of(
                        ComponentTeam.builder().teamId(team.getId()).type(ComponentTeamType.PRIMARY).build()))
                .build();
        Map<String, Component> components = Map.ofEntries(createMapEntry(component1));

        // When
        Area returnValue = areaMethod.call(underTest, area, teams, components);

        // Then
        assertThat(returnValue.getName()).isEqualTo("Area Name 1");
        assertThat(returnValue.getTeams()).containsExactly(team);
        assertThat(returnValue.getComponents()).containsExactly(component1);
    }
    
    private <V extends ObjectWithId> Map.Entry<String, V> createMapEntry(V value) {
        return Map.entry(value.getId(), value);
    }

    private <V extends ObjectWithId> void addMapEntry(Map<String, V> map, V value) {
        map.put(value.getId(), value);
    }

    private static Stream<TeamMethod> provideTeamMethods() {
        return Stream.of(
                (underTest, team, components) -> {
                    List<Team> returnValue = underTest.toSortedUnmodifiableTeamList(Stream.of(team), components);
                    assertThat(returnValue).hasSize(1);
                    return returnValue.get(0);
                },
                ComponentMetadataAssembler::addNestedItemsToTeam);
    }

    private static Stream<AreaMethod> provideAreaMethods() {
        return Stream.of(
                (underTest, area, teams, components) -> {
                    List<Area> returnValue = underTest.toSortedUnmodifiableAreaList(Stream.of(area), teams, components);
                    assertThat(returnValue).hasSize(1);
                    return returnValue.get(0);
                },
                ComponentMetadataAssembler::addNestedItemsToArea);
    }

    @FunctionalInterface
    private interface TeamMethod {

        Team call(ComponentMetadataAssembler underTest, Team team, Map<String, Component> components);
    }

    @FunctionalInterface
    private interface AreaMethod {

        Area call(ComponentMetadataAssembler underTest, Area area, Map<String, Team> teams, Map<String, Component> components);
    }
}