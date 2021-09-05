package tech.kronicle.service.services;

import tech.kronicle.sdk.models.Area;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Scanner;
import tech.kronicle.sdk.models.Summary;
import tech.kronicle.sdk.models.SummaryCallGraph;
import tech.kronicle.sdk.models.SummarySubComponentDependencyNode;
import tech.kronicle.sdk.models.Team;
import tech.kronicle.sdk.models.TestOutcome;
import tech.kronicle.sdk.models.TestResult;
import tech.kronicle.service.repositories.ComponentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ComponentServiceTest {

    @Mock
    private ComponentRepository mockComponentRepository;
    private ComponentService underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new ComponentService(mockComponentRepository);
    }
    
    @Test
    public void getComponentsShouldReturnAllComponents() {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .build();
        Component component2 = Component.builder()
                .id("test-component-id-2")
                .build();
        when(mockComponentRepository.getComponents()).thenReturn(List.of(component1, component2));

        // When
        List<Component> returnValue = underTest.getComponents(Optional.empty(), Optional.empty(), List.of());

        // Then
        assertThat(returnValue).containsExactly(component1, component2);
    }

    @Test
    public void getComponentsShouldReturnComponentsFromOffset() {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .testResults(List.of(new TestResult(null, TestOutcome.FAIL, null, null)))
                .build();
        Component component2 = Component.builder()
                .id("test-component-id-2")
                .build();
        Component component3= Component.builder()
                .id("test-component-id-3")
                .build();
        when(mockComponentRepository.getComponents()).thenReturn(List.of(component1, component2, component3));

        // When
        List<Component> returnValue = underTest.getComponents(Optional.of(1), Optional.empty(), List.of());

        // Then
        assertThat(returnValue).containsExactly(component2, component3);
    }

    @Test
    public void getComponentsShouldIgnoreOffsetBeyondSize() {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .build();
        Component component2 = Component.builder()
                .id("test-component-id-2")
                .build();
        List<Component> components = List.of(component1, component2);
        when(mockComponentRepository.getComponents()).thenReturn(components);

        // When
        List<Component> returnValue = underTest.getComponents(Optional.of(components.size() + 1), Optional.empty(), List.of());

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getComponentsShouldReturnComponentsUpToLimit() {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .build();
        Component component2 = Component.builder()
                .id("test-component-id-2")
                .build();
        Component component3 = Component.builder()
                .id("test-component-id-3")
                .build();
        when(mockComponentRepository.getComponents()).thenReturn(List.of(component1, component2, component3));

        // When
        List<Component> returnValue = underTest.getComponents(Optional.empty(), Optional.of(2), List.of());

        // Then
        assertThat(returnValue).containsExactly(component1, component2);
    }

    @Test
    public void getComponentsShouldReturnComponentsFromOffsetUpToLimit() {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .build();
        Component component2 = Component.builder()
                .id("test-component-id-2")
                .build();
        Component component3 = Component.builder()
                .id("test-component-id-3")
                .build();
        Component component4 = Component.builder()
                .id("test-component-id-4")
                .build();
        when(mockComponentRepository.getComponents()).thenReturn(List.of(component1, component2, component3, component4));

        // When
        List<Component> returnValue = underTest.getComponents(Optional.of(1), Optional.of(2), List.of());

        // Then
        assertThat(returnValue).containsExactly(component2, component3);
    }

    @Test
    public void getComponentsShouldReturnComponentsFilteredByAllFilters() {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .build();
        Component component2 = Component.builder()
                .id("test-component-id-2")
                .testResults(List.of(new TestResult(null, TestOutcome.FAIL, null, null)))
                .build();
        Component component3 = Component.builder()
                .id("test-component-id-3")
                .testResults(List.of(new TestResult(null, TestOutcome.PASS, null, null)))
                .build();
        Component component4 = Component.builder()
                .id("test-component-id-4")
                .build();
        when(mockComponentRepository.getComponents()).thenReturn(List.of(component1, component2, component3, component4));

        // When
        List<Component> returnValue = underTest.getComponents(Optional.of(1), Optional.of(2), List.of(TestOutcome.FAIL));

        // Then
        assertThat(returnValue).containsExactly(component2);
    }

    @Test
    public void getComponentShouldReturnTheComponentWithMatchingId() {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id")
                .build();
        when(mockComponentRepository.getComponent(component1.getId())).thenReturn(component1);

        // When
        Component returnValue = underTest.getComponent(component1.getId());

        // Then
        assertThat(returnValue).isSameAs(component1);
    }

    @Test
    public void getComponentShouldReturnNullIfNoComponentMatchesId() {
        // Given
        String componentId = "test-component-id";
        when(mockComponentRepository.getComponent(componentId)).thenReturn(null);

        // When
        Component returnValue = underTest.getComponent(componentId);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getComponentNodesShouldReturnTheComponentNodesForAComponent() {
        // Given
        String componentId = "test-component-id-1";
        SummarySubComponentDependencyNode node1 = SummarySubComponentDependencyNode.builder()
                .componentId(componentId)
                .spanName("test-span-name-1")
                .build();
        SummarySubComponentDependencyNode node2 = SummarySubComponentDependencyNode.builder()
                .componentId(componentId)
                .spanName("test-span-name-2")
                .build();
        List<SummarySubComponentDependencyNode> nodes = List.of(node1, node2);
        when(mockComponentRepository.getComponentNodes(componentId)).thenReturn(nodes);

        // When
        List<SummarySubComponentDependencyNode> returnValue = underTest.getComponentNodes(componentId);

        // Then
        assertThat(returnValue).isSameAs(nodes);
    }

    @Test
    public void getComponentCallGraphsShouldReturnTheComponentCallGraphsForAComponent() {
        // Given
        String componentId = "test-component-id-1";
        SummaryCallGraph callGraph1 = SummaryCallGraph.builder()
                .traceCount(1)
                .build();
        SummaryCallGraph callGraph2 = SummaryCallGraph.builder()
                .traceCount(2)
                .build();
        List<SummaryCallGraph> callGraphs = List.of(callGraph1, callGraph2);
        when(mockComponentRepository.getComponentCallGraphs(componentId)).thenReturn(callGraphs);

        // When
        List<SummaryCallGraph> returnValue = underTest.getComponentCallGraphs(componentId);

        // Then
        assertThat(returnValue).isSameAs(callGraphs);
    }

    @Test
    public void getTeamsShouldReturnAllTeams() {
        // Given
        Team team1 = Team.builder()
                .id("test-team-id-1")
                .build();
        Team team2 = Team.builder()
                .id("test-team-id-2")
                .build();
        List<Team> teams = List.of(team1, team2);
        when(mockComponentRepository.getTeams()).thenReturn(teams);

        // When
        List<Team> returnValue = underTest.getTeams(List.of());

        // Then
        assertThat(returnValue).containsExactly(team1, team2);
    }

    @Test
    public void getTeamShouldReturnTheTeamWithMatchingId() {
        // Given
        Team team1 = Team.builder()
                .id("test-team-id")
                .build();
        when(mockComponentRepository.getTeam(team1.getId())).thenReturn(team1);

        // When
        Team returnValue = underTest.getTeam(team1.getId(), List.of());

        // Then
        assertThat(returnValue).isEqualTo(team1);
    }

    @Test
    public void getTeamShouldReturnNullIfNoTeamMatchesId() {
        // Given
        String teamId = "test-team-id";
        when(mockComponentRepository.getTeam(teamId)).thenReturn(null);

        // When
        Team returnValue = underTest.getTeam(teamId, List.of());

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getAreasShouldReturnAllAreas() {
        // Given
        Area area1 = Area.builder()
                .id("test-area-id-1")
                .build();
        Area area2 = Area.builder()
                .id("test-area-id-2")
                .build();
        List<Area> areas = List.of(area1, area2);
        when(mockComponentRepository.getAreas()).thenReturn(areas);

        // When
        List<Area> returnValue = underTest.getAreas(List.of());

        // Then
        assertThat(returnValue).containsExactly(area1, area2);
    }

    @Test
    public void getAreaShouldReturnTheAreaWithMatchingId() {
        // Given
        Area area1 = Area.builder()
                .id("test-area-id")
                .build();
        when(mockComponentRepository.getArea(area1.getId())).thenReturn(area1);

        // When
        Area returnValue = underTest.getArea(area1.getId(), List.of());

        // Then
        assertThat(returnValue).isEqualTo(area1);
    }

    @Test
    public void getAreaShouldReturnNullIfNoAreaMatchesId() {
        // Given
        String areaId = "test-area-id";
        when(mockComponentRepository.getArea(areaId)).thenReturn(null);

        // When
        Area returnValue = underTest.getArea(areaId, List.of());

        // Then
        assertThat(returnValue).isNull();
    }

    @ParameterizedTest
    @MethodSource("provideComponentsReturningMethods")
    public void componentsReturningMethodsShouldReturnAllComponentsWhenNoTestResultFilterIsSpecified(ComponentsReturningMethod methodWrapper) {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .testResults(List.of(TestResult.builder().outcome(TestOutcome.FAIL).build()))
                .build();
        Component component2 = Component.builder()
                .id("test-component-id-2")
                .build();
        List<Component> components = List.of(component1, component2);

        // When
        List<Component> returnValue = methodWrapper.call(underTest, mockComponentRepository, components, List.of());

        // Then
        assertThat(returnValue).containsExactly(component1, component2);
    }

    @ParameterizedTest
    @MethodSource("provideComponentsReturningMethods")
    public void componentsReturningMethodsShouldNotReturnComponentWithEmptyTestResultsWhenTestResultFilterIsSpecified(ComponentsReturningMethod methodWrapper) {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .testResults(List.of())
                .build();
        Component component2 = Component.builder()
                .id("test-component-id-2")
                .testResults(List.of(new TestResult("test-id-1", TestOutcome.PASS, null, null)))
                .build();
        List<Component> components = List.of(component1, component2);

        // When
        List<Component> returnValue = methodWrapper.call(underTest, mockComponentRepository, components, List.of(TestOutcome.PASS));

        // Then
        assertThat(returnValue).containsExactly(component2);
    }

    @ParameterizedTest
    @MethodSource("provideComponentsReturningMethods")
    public void componentsReturningMethodsShouldNotReturnComponentWithAnUnwantedTestResultWhenTestResultFilterIsSpecified(ComponentsReturningMethod methodWrapper) {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .testResults(List.of(new TestResult("test-id-1", TestOutcome.FAIL, null, null)))
                .build();
        Component component2 = Component.builder()
                .id("test-component-id-2")
                .testResults(List.of(new TestResult("test-id-2", TestOutcome.PASS, null, null)))
                .build();
        List<Component> components = List.of(component1, component2);

        // When
        List<Component> returnValue = methodWrapper.call(underTest, mockComponentRepository, components, List.of(TestOutcome.PASS));

        // Then
        assertThat(returnValue).containsExactly(component2);
    }

    @ParameterizedTest
    @MethodSource("provideComponentsReturningMethods")
    public void componentsReturningMethodsShouldReturnOnlyTestResultsOfAComponentThatMatchTestOutcomeFilter(ComponentsReturningMethod methodWrapper) {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .testResults(List.of(
                        new TestResult("test-id-1", TestOutcome.FAIL, null, null),
                        new TestResult("test-id-2", TestOutcome.PASS, null, null),
                        new TestResult("test-id-3", TestOutcome.NOT_APPLICABLE, null, null)))
                .build();
        List<Component> components = List.of(component1);

        // When
        List<Component> returnValue = methodWrapper.call(underTest, mockComponentRepository, components, List.of(TestOutcome.PASS));

        // Then
        assertThat(returnValue).containsExactly(Component.builder()
                .id("test-component-id-1")
                .testResults(List.of(
                        new TestResult("test-id-2", TestOutcome.PASS, null, null)))
                .build());
    }

    @ParameterizedTest
    @MethodSource("provideComponentsReturningMethods")
    public void componentsReturningMethodsShouldReturnComponentsWithEitherTestOutcomeWhenFilteringBy2TestOutcomes(ComponentsReturningMethod methodWrapper) {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .testResults(List.of(
                        new TestResult("test-id-1", TestOutcome.FAIL, null, null)))
                .build();
        Component component2 = Component.builder()
                .id("test-component-id-2")
                .testResults(List.of(
                        new TestResult("test-id-2", TestOutcome.PASS, null, null)))
                .build();
        Component component3 = Component.builder()
                .id("test-component-id-3")
                .testResults(List.of(
                        new TestResult("test-id-3", TestOutcome.NOT_APPLICABLE, null, null)))
                .build();
        List<Component> components = List.of(component1, component2, component3);

        // When
        List<Component> returnValue = methodWrapper.call(underTest, mockComponentRepository, components, List.of(TestOutcome.FAIL, TestOutcome.PASS));

        // Then
        assertThat(returnValue).containsExactly(component1, component2);
    }

    @Test
    public void getScannersShouldReturnTheScanners() {
        // Given
        List<Scanner> scanners = List.of(Scanner.builder().build());
        when(mockComponentRepository.getScanners()).thenReturn(scanners);

        // When
        List<Scanner> returnValue = underTest.getScanners();

        // Then
        assertThat(returnValue).isSameAs(scanners);
    }

    @Test
    public void getScannerShouldReturnAScanner() {
        // Given
        Scanner scanner = Scanner.builder().id("test-scanner-1").build();
        when(mockComponentRepository.getScanner(scanner.getId())).thenReturn(scanner);

        // When
        Scanner returnValue = underTest.getScanner(scanner.getId());

        // Then
        assertThat(returnValue).isSameAs(scanner);
    }


    @Test
    public void getScannerShouldReturnNullIfNoScannerMatchesId() {
        // Given
        String scannerId = "test-scanner-id";
        when(mockComponentRepository.getScanner(scannerId)).thenReturn(null);

        // When
        Scanner returnValue = underTest.getScanner(scannerId);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getTestsShouldReturnAllTests() {
        // Given
        tech.kronicle.sdk.models.Test test1 = tech.kronicle.sdk.models.Test.builder()
                .id("test-test-id-1")
                .build();
        tech.kronicle.sdk.models.Test test2 = tech.kronicle.sdk.models.Test.builder()
                .id("test-test-id-2")
                .build();
        List<tech.kronicle.sdk.models.Test> tests = List.of(test1, test2);
        when(mockComponentRepository.getTests()).thenReturn(tests);

        // When
        List<tech.kronicle.sdk.models.Test> returnValue = underTest.getTests();

        // Then
        assertThat(returnValue).containsExactly(test1, test2);
    }

    @Test
    public void getTestShouldReturnTheTestWithMatchingId() {
        // Given
        tech.kronicle.sdk.models.Test test1 = tech.kronicle.sdk.models.Test.builder()
                .id("test-test-id")
                .build();
        when(mockComponentRepository.getTest(test1.getId())).thenReturn(test1);

        // When
        tech.kronicle.sdk.models.Test returnValue = underTest.getTest(test1.getId());

        // Then
        assertThat(returnValue).isEqualTo(test1);
    }

    @Test
    public void getTestShouldReturnNullIfNoTestMatchesId() {
        // Given
        String testId = "test-test-id";
        when(mockComponentRepository.getTest(testId)).thenReturn(null);

        // When
        tech.kronicle.sdk.models.Test returnValue = underTest.getTest(testId);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getSummaryShouldReturnTheSummary() {
        // Given
        Summary summary = Summary.EMPTY;
        when(mockComponentRepository.getSummary()).thenReturn(summary);

        // When
        Summary returnValue = underTest.getSummary();

        // Then
        assertThat(returnValue).isSameAs(summary);
    }

    private static Stream<ComponentsReturningMethod> provideComponentsReturningMethods() {
        return Stream.of(
                (underTest, mockComponentRepository, components, testOutcomes) -> {
                    when(mockComponentRepository.getComponents()).thenReturn(components);
                    return underTest.getComponents(Optional.empty(), Optional.empty(), testOutcomes);
                },
                (underTest, mockComponentRepository, components, testOutcomes) -> {
                    when(mockComponentRepository.getTeams()).thenReturn(List.of(createTeam(components)));
                    List<Team> returnValue = underTest.getTeams(testOutcomes);
                    assertThat(returnValue).hasSize(1);
                    return returnValue.get(0).getComponents();
                },
                (underTest, mockComponentRepository, components, testOutcomes) -> {
                    Team team = createTeam(components);
                    when(mockComponentRepository.getTeam(team.getId())).thenReturn(team);
                    Team returnValue = underTest.getTeam(team.getId(), testOutcomes);
                    return returnValue.getComponents();
                },
                (underTest, mockComponentRepository, components, testOutcomes) -> {
                    when(mockComponentRepository.getAreas()).thenReturn(List.of(createArea(components)));
                    List<Area> returnValue = underTest.getAreas(testOutcomes);
                    assertThat(returnValue).hasSize(1);
                    return returnValue.get(0).getComponents();
                },
                (underTest, mockComponentRepository, components, testOutcomes) -> {
                    Area area = createArea(components);
                    when(mockComponentRepository.getArea(area.getId())).thenReturn(area);
                    Area returnValue = underTest.getArea(area.getId(), testOutcomes);
                    return returnValue.getComponents();
                });
    }
    
    private static Team createTeam(List<Component> components) {
        return Team.builder()
                .id("test-team-id")
                .components(components)
                .build();
    }

    private static Area createArea(List<Component> components) {
        return Area.builder()
                .id("test-area-id")
                .components(components)
                .build();
    }

    @FunctionalInterface
    private interface ComponentsReturningMethod {
        
        List<Component> call(ComponentService underTest, ComponentRepository mockComponentRepository, List<Component> components, List<TestOutcome> testOutcomes);
    }
}
