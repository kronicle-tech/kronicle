package tech.kronicle.service.repositories;

import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.Scheduled;
import tech.kronicle.sdk.models.*;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.service.testutils.DiagramUtils;
import tech.kronicle.testutils.LogCaptor;
import tech.kronicle.utils.ObjectReference;
import tech.kronicle.service.services.ComponentMetadataAssembler;
import tech.kronicle.service.services.ComponentMetadataLoader;
import tech.kronicle.service.services.ScanEngine;
import tech.kronicle.service.services.ScannerExtensionRegistry;
import tech.kronicle.service.services.TestEngine;
import tech.kronicle.service.services.TestFinder;
import tech.kronicle.service.services.ValidatorService;
import tech.kronicle.service.tests.models.TestContext;
import tech.kronicle.service.testutils.ValidatorServiceFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tech.kronicle.service.testutils.ComponentUtils.*;
import static tech.kronicle.service.testutils.DiagramUtils.createDiagram;
import static tech.kronicle.service.testutils.DiagramUtils.createDiagramId;

@ExtendWith(MockitoExtension.class)
public class ComponentRepositoryTest {

    @Mock
    private ComponentMetadataRepository mockComponentMetadataRepository;
    private ComponentMetadataLoader componentMetadataLoaderSpy;
    @Mock
    private ScanEngine mockScanEngine;
    @Mock
    private ScannerExtensionRegistry mockScannerRegistry;
    @Mock
    private TestEngine mockTestEngine;
    @Mock
    private TestFinder mockTestFinder;
    private ComponentRepository underTest;

    @BeforeEach
    public void beforeEach() {
        ValidatorService validatorService = ValidatorServiceFactory.createValidationService();
        componentMetadataLoaderSpy = Mockito.spy(new ComponentMetadataLoader(validatorService));
        underTest = new ComponentRepository(mockComponentMetadataRepository, componentMetadataLoaderSpy, new ComponentMetadataAssembler(), mockScanEngine,
                mockScannerRegistry, mockTestEngine, mockTestFinder);
    }

    @ParameterizedTest
    @MethodSource("provideInitializeAndRefreshScenarios")
    public void shouldLoadComponentMetadataItems(Consumer<ComponentRepository> action) {
        // Given
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(
                ComponentMetadata.builder()
                        .areas(List.of(Area.builder()
                                .id("test-area-id")
                                .name("Test Area Name")
                                .build()))
                        .teams(List.of(Team.builder()
                                .id("test-team-id")
                                .name("Test Team Name")
                                .areaId("test-area-id")
                                .emailAddress("test-team@example.com")
                                .build()))
                        .components(List.of(Component.builder()
                                .id("test-component-id")
                                .name("Test Component Name")
                                .type("test-component-type-id")
                                .tags(List.of(
                                        Tag.builder().key("test-component-tag-1").build(),
                                        Tag.builder().key("test-component-tag-2").build()
                                ))
                                .repo(RepoReference.builder().url("https://example.com/example.git").build())
                                .notes("Test Component Notes")
                                .teams(List.of(ComponentTeam.builder().teamId("test-team-id").build()))
                                .platformId("test-platform-id")
                                .build()))
                        .build());

        // When
        action.accept(underTest);

        // Then
        List<Area> areas = underTest.getAreas();
        assertThat(areas).hasSize(1);
        Area area;
        area = areas.get(0);
        assertThat(area.getId()).isEqualTo("test-area-id");
        assertThat(area.getName()).isEqualTo("Test Area Name");

        List<Team> teams = underTest.getTeams();
        assertThat(teams).hasSize(1);
        Team team;
        team = teams.get(0);
        assertThat(team.getId()).isEqualTo("test-team-id");
        assertThat(team.getName()).isEqualTo("Test Team Name");
        assertThat(team.getAreaId()).isEqualTo("test-area-id");
        assertThat(team.getEmailAddress()).isEqualTo("test-team@example.com");

        List<Component> components = underTest.getComponents();
        assertThat(components).hasSize(1);
        Component component;
        component = components.get(0);
        assertThat(component.getId()).isEqualTo("test-component-id");
        assertThat(component.getName()).isEqualTo("Test Component Name");
        assertThat(component.getType()).isEqualTo("test-component-type-id");
        assertThat(component.getTags()).containsExactly(
                Tag.builder().key("test-component-tag-1").build(),
                Tag.builder().key("test-component-tag-2").build()
        );
        assertThat(component.getRepo().getUrl()).isEqualTo("https://example.com/example.git");
        assertThat(component.getNotes()).isEqualTo("Test Component Notes");
        assertThat(component.getTeams()).containsExactly(new ComponentTeam("test-team-id", null, null));
        assertThat(component.getPlatformId()).isEqualTo("test-platform-id");
        assertThat(component.getScannerErrors()).isEmpty();
    }

    @Test
    public void initializeShouldStartAScanAndUpdateSummaryAndRefreshShouldToo() {
        // Given
        assertThat(underTest.getSummary()).isEqualTo(Summary.EMPTY);
        ComponentMetadata componentMetadata = ComponentMetadata
                .builder()
                .components(List.of(
                        createComponent(1),
                        createComponent(2)))
                .diagrams(List.of(
                        createDiagram(1),
                        createDiagram(2)))
                .build();
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(componentMetadata);
        ObjectReference<ComponentMetadataLoader.Output> loaderOutput = new ObjectReference<>();
        when(componentMetadataLoaderSpy.loadComponentMetadata(componentMetadata)).then(answer -> {
            ComponentMetadataLoader.Output returnValue = (ComponentMetadataLoader.Output) answer.callRealMethod();
            loaderOutput.set(returnValue);
            return returnValue;
        });
        doAnswer(invocation -> {
            assertDuringInitializeTheComponentsAndSummaryAreUpdatedStraightAway(
                    getComponentMapArgument(invocation),
                    getDiagramMapArgument(invocation),
                    getSummaryConsumerArgument(invocation)
            );
            return null;
        }).when(mockScanEngine).scan(eq(componentMetadata), any(), any(), any());

        // When
        initializeAndWaitForRefreshToFinish(underTest);

        // Then
        verify(mockScanEngine).scan(
                eq(componentMetadata),
                eq(loaderOutput.get().getComponents()),
                eq(loaderOutput.get().getDiagrams()),
                any()
        );
        verify(mockTestEngine).test(eq(loaderOutput.get().getComponents()));
        assertThat(underTest.getComponents()).hasSize(4);
        assertThat(underTest.getComponents().stream().map(Component::getId)).containsExactly(
                "test-component-id-1",
                "test-component-id-2",
                "test-component-id-3",
                "test-component-id-4");
        assertThat(underTest.getSummary()).isEqualTo(createTestSummary("initialize-2"));

        // Given
        componentMetadata = ComponentMetadata
                .builder()
                .components(List.of(
                        createComponent(5),
                        createComponent(6)))
                .build();
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(componentMetadata);
        doAnswer(invocation -> {
            assertDuringRefreshTheComponentsAndSummaryAreUpdatedOnlyAtTheEnd(
                    getComponentMapArgument(invocation),
                    getDiagramMapArgument(invocation),
                    getSummaryConsumerArgument(invocation)
            );
            return null;
        }).when(mockScanEngine).scan(eq(componentMetadata), any(), any(), any());

        // When
        underTest.refresh();

        // Then
        verify(mockScanEngine).scan(eq(componentMetadata), any(), any(), any());
        verify(mockTestEngine).test(eq(loaderOutput.get().getComponents()));
        assertThat(underTest.getComponents()).hasSize(4);
        assertThat(underTest.getComponents().stream().map(Component::getId)).containsExactly(
                "test-component-id-5",
                "test-component-id-6",
                "test-component-id-7",
                "test-component-id-8");
        assertThat(underTest.getSummary()).isEqualTo(createTestSummary("refresh-2"));
    }

    @Test
    public void refreshShouldBeScheduledToRunEveryMinute() throws NoSuchMethodException {
        // When
        Method refreshMethod = underTest.getClass().getMethod("refresh");
        Scheduled scheduledAnnotation = refreshMethod.getAnnotation(Scheduled.class);

        // Then
        assertThat(scheduledAnnotation).isNotNull();
        assertThat(scheduledAnnotation.cron()).isEqualTo("0 */1 * * * *");
        assertThat(scheduledAnnotation.zone()).isEqualTo("UTC");
        assertThat(scheduledAnnotation.zone()).isEqualTo("UTC");
        assertThat(scheduledAnnotation.fixedDelay()).isEqualTo(-1);
        assertThat(scheduledAnnotation.fixedDelayString()).isEqualTo("");
        assertThat(scheduledAnnotation.fixedRate()).isEqualTo(-1);
        assertThat(scheduledAnnotation.fixedRateString()).isEqualTo("");
        assertThat(scheduledAnnotation.initialDelay()).isEqualTo(-1);
        assertThat(scheduledAnnotation.initialDelayString()).isEqualTo("");
    }

    @Test
    public void getAreasShouldReturnAllAreas() {
        // Given
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(createComponentMetadata());
        initializeAndWaitForRefreshToFinish(underTest);

        // When
        List<Area> areas = underTest.getAreas();

        // Then
        assertThat(areas).hasSize(2);
        Area area;
        Team team;
        Component component;
        area = areas.get(0);
        assertThat(area.getId()).isEqualTo("test-area-id-1");
        assertThat(area.getTeams()).hasSize(1);
        team = area.getTeams().get(0);
        assertThat(team.getId()).isEqualTo("test-team-id-1");
        assertThat(area.getComponents()).hasSize(1);
        component = area.getComponents().get(0);
        assertThat(component.getId()).isEqualTo("test-component-id-1");
        area = areas.get(1);
        assertThat(area.getId()).isEqualTo("test-area-id-2");
        assertThat(area.getTeams()).hasSize(1);
        team = area.getTeams().get(0);
        assertThat(team.getId()).isEqualTo("test-team-id-2");
        assertThat(area.getComponents()).hasSize(1);
        component = area.getComponents().get(0);
        assertThat(component.getId()).isEqualTo("test-component-id-2");
    }

    @Test
    public void getAreasShouldReturnAreasSortedByName() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .areas(List.of(
                        // Area names are deliberately not in alphabetical order
                        Area.builder().id("test-area-id-a").name("Test Area Name A").build(),
                        Area.builder().id("test-area-id-c").name("Test Area Name C").build(),
                        Area.builder().id("test-area-id-b").name("Test Area Name B").build()))
                .build();
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(componentMetadata);
        initializeAndWaitForRefreshToFinish(underTest);

        // When
        List<Area> areas = underTest.getAreas();

        // Then
        assertThat(areas).hasSize(3);
        assertThat(areas.get(0).getName()).isEqualTo("Test Area Name A");
        assertThat(areas.get(1).getName()).isEqualTo("Test Area Name B");
        assertThat(areas.get(2).getName()).isEqualTo("Test Area Name C");
    }

    @Test
    public void getAreasShouldReturnAnUnmodifiableList() {
        // Given
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(createComponentMetadata());
        initializeAndWaitForRefreshToFinish(underTest);
        initializeAndWaitForRefreshToFinish(underTest);

        // When
        Throwable thrown = catchThrowable(() -> underTest.getAreas().add(Area.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void getAreaShouldReturnTheAreaWithMatchingAreaId() {
        // Given
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(createComponentMetadata());
        String areaId = "test-area-id-2";
        initializeAndWaitForRefreshToFinish(underTest);

        // When
        Area area = underTest.getArea(areaId);

        // Then
        assertThat(area).isNotNull();
        Team team;
        Component component;
        assertThat(area.getId()).isEqualTo("test-area-id-2");
        assertThat(area.getTeams()).hasSize(1);
        team = area.getTeams().get(0);
        assertThat(team.getId()).isEqualTo("test-team-id-2");
        assertThat(area.getComponents()).hasSize(1);
        component = area.getComponents().get(0);
        assertThat(component.getId()).isEqualTo("test-component-id-2");
    }

    @Test
    public void getTeamsShouldReturnAllTeams() {
        // Given
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(createComponentMetadata());
        initializeAndWaitForRefreshToFinish(underTest);

        // When
        List<Team> teams = underTest.getTeams();

        // Then
        assertThat(teams).hasSize(2);
        Team team;
        Component component;
        team = teams.get(0);
        assertThat(team.getId()).isEqualTo("test-team-id-1");
        assertThat(team.getComponents()).hasSize(1);
        component = team.getComponents().get(0);
        assertThat(component.getId()).isEqualTo("test-component-id-1");
        team = teams.get(1);
        assertThat(team.getId()).isEqualTo("test-team-id-2");
        assertThat(team.getComponents()).hasSize(1);
        component = team.getComponents().get(0);
        assertThat(component.getId()).isEqualTo("test-component-id-2");
    }

    @Test
    public void getTeamsShouldReturnTeamsSortedByName() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .teams(List.of(
                        // Team names are deliberately not in alphabetical order
                        Team.builder().id("test-team-id-a").name("Test Team Name A").emailAddress("test-team-a@example.com").build(),
                        Team.builder().id("test-team-id-c").name("Test Team Name C").emailAddress("test-team-c@example.com").build(),
                        Team.builder().id("test-team-id-b").name("Test Team Name B").emailAddress("test-team-b@example.com").build()))
                .build();
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(componentMetadata);
        initializeAndWaitForRefreshToFinish(underTest);

        // When
        List<Team> teams = underTest.getTeams();

        // Then
        assertThat(teams).hasSize(3);
        assertThat(teams.get(0).getName()).isEqualTo("Test Team Name A");
        assertThat(teams.get(1).getName()).isEqualTo("Test Team Name B");
        assertThat(teams.get(2).getName()).isEqualTo("Test Team Name C");
    }

    @Test
    public void getTeamsShouldReturnAnUnmodifiableList() {
        // Given
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(createComponentMetadata());
        initializeAndWaitForRefreshToFinish(underTest);
        initializeAndWaitForRefreshToFinish(underTest);

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTeams().add(Team.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void getTeamShouldReturnTheTeamWithMatchingTeamId() {
        // Given
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(createComponentMetadata());
        String teamId = "test-team-id-2";
        initializeAndWaitForRefreshToFinish(underTest);

        // When
        Team team = underTest.getTeam(teamId);

        // Then
        assertThat(team).isNotNull();
        Component component;
        assertThat(team.getId()).isEqualTo("test-team-id-2");
        assertThat(team.getComponents()).hasSize(1);
        component = team.getComponents().get(0);
        assertThat(component.getId()).isEqualTo("test-component-id-2");
    }

    @Test
    public void getComponentsShouldReturnAllComponents() {
        // Given
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(createComponentMetadata());
        initializeAndWaitForRefreshToFinish(underTest);

        // When
        List<Component> components = underTest.getComponents();

        // Then
        assertThat(components).hasSize(2);
        Component component;
        component = components.get(0);
        assertThat(component.getId()).isEqualTo("test-component-id-1");
        component = components.get(1);
        assertThat(component.getId()).isEqualTo("test-component-id-2");
    }

    @Test
    public void getComponentsShouldReturnComponentsSortedByName() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        // Component names are deliberately not in alphabetical order
                        createComponent(1),
                        createComponent(3),
                        createComponent(2)))
                .build();
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(componentMetadata);
        initializeAndWaitForRefreshToFinish(underTest);

        // When
        List<Component> components = underTest.getComponents();

        // Then
        assertThat(components).hasSize(3);
        assertThat(components.get(0).getName()).isEqualTo("Test Component Name 1");
        assertThat(components.get(1).getName()).isEqualTo("Test Component Name 2");
        assertThat(components.get(2).getName()).isEqualTo("Test Component Name 3");
    }

    @Test
    public void getComponentsShouldReturnAnUnmodifiableList() {
        // Given
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(createComponentMetadata());
        initializeAndWaitForRefreshToFinish(underTest);
        initializeAndWaitForRefreshToFinish(underTest);

        // When
        Throwable thrown = catchThrowable(() -> underTest.getComponents().add(Component.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void getComponentShouldReturnTheComponentWithMatchingComponentId() {
        // Given
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(createComponentMetadata());
        String componentId = "test-component-id-2";
        initializeAndWaitForRefreshToFinish(underTest);

        // When
        Component component = underTest.getComponent(componentId);

        // Then
        assertThat(component).isNotNull();
        assertThat(component.getId()).isEqualTo("test-component-id-2");
        assertThat(component.getName()).isEqualTo("Test Component Name 2");
    }

    @Test
    public void getComponentDiagramsShouldReturnTheDiagramsWithAnyMatchingNodesWithMatchingComponentId() {
        // Given
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(ComponentMetadata.builder().build());
        Diagram diagram1 = createDiagram(1, List.of(
                new DiagramUtils.ComponentNumbersForConnection(1, 2)
        ));
        Diagram diagram2 = createDiagram(2, List.of(
                new DiagramUtils.ComponentNumbersForConnection(2, 3)
        ));
        Diagram diagram3 = createDiagram(3, List.of(
                new DiagramUtils.ComponentNumbersForConnection(3, 4)
        ));
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(
                ComponentMetadata.builder()
                        .diagrams(List.of(
                                diagram1,
                                diagram2,
                                diagram3
                        ))
                        .build()
        );
        initializeAndWaitForRefreshToFinish(underTest);

        // When
        List<Diagram> returnValue = underTest.getComponentDiagrams("test-component-id-2");

        // Then
        assertThat(returnValue).containsExactly(diagram1, diagram2);
    }

    @Test
    public void getDiagramsShouldReturnAllDiagrams() {
        // Given
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(createComponentMetadata());
        initializeAndWaitForRefreshToFinish(underTest);

        // When
        List<Diagram> diagrams = underTest.getDiagrams();

        // Then
        assertThat(diagrams).hasSize(2);
        Diagram diagram;
        diagram = diagrams.get(0);
        assertThat(diagram.getId()).isEqualTo("test-diagram-id-1");
        diagram = diagrams.get(1);
        assertThat(diagram.getId()).isEqualTo("test-diagram-id-2");
    }

    @Test
    public void getDiagramsShouldReturnDiagramsSortedByName() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .diagrams(List.of(
                        // Diagram names are deliberately not in alphabetical order
                        createDiagram(1),
                        createDiagram(3),
                        createDiagram(2)))
                .build();
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(componentMetadata);
        initializeAndWaitForRefreshToFinish(underTest);

        // When
        List<Diagram> diagrams = underTest.getDiagrams();

        // Then
        assertThat(diagrams).hasSize(3);
        assertThat(diagrams.get(0).getName()).isEqualTo("Test Diagram Name 1");
        assertThat(diagrams.get(1).getName()).isEqualTo("Test Diagram Name 2");
        assertThat(diagrams.get(2).getName()).isEqualTo("Test Diagram Name 3");
    }

    @Test
    public void getDiagramsShouldReturnAnUnmodifiableList() {
        // Given
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(createComponentMetadata());
        initializeAndWaitForRefreshToFinish(underTest);
        initializeAndWaitForRefreshToFinish(underTest);

        // When
        Throwable thrown = catchThrowable(() -> underTest.getDiagrams().add(Diagram.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void getDiagramShouldReturnTheDiagramWithMatchingDiagramId() {
        // Given
        when(mockComponentMetadataRepository.getComponentMetadata()).thenReturn(createComponentMetadata());
        String diagramId = "test-diagram-id-2";
        initializeAndWaitForRefreshToFinish(underTest);

        // When
        Diagram diagram = underTest.getDiagram(diagramId);

        // Then
        assertThat(diagram).isNotNull();
        assertThat(diagram.getId()).isEqualTo("test-diagram-id-2");
        assertThat(diagram.getName()).isEqualTo("Test Diagram Name 2");
    }

    @Test
    public void getScannersShouldHandleAnEmptyListOfScanners() {
        // Given
        when(mockScannerRegistry.getAllItems()).thenReturn(List.of());

        // When
        List<Scanner> returnValue = underTest.getScanners();

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getScannersShouldConvertAListOfScannersAndSortThenById() {
        // Given
        when(mockScannerRegistry.getAllItems()).thenReturn(List.of(
                new TestScanner("test-id-c", "Test Description 1", "Test Notes 1"),
                new TestScanner("test-id-a", "Test Description 2", "Test Notes 2"),
                new TestScanner("test-id-b", "Test Description 3", "Test Notes 3")));

        // When
        List<Scanner> returnValue = underTest.getScanners();

        // Then
        assertThat(returnValue).containsExactly(
                new Scanner("test-id-a", "Test Description 2", "Test Notes 2"),
                new Scanner("test-id-b", "Test Description 3", "Test Notes 3"),
                new Scanner("test-id-c", "Test Description 1", "Test Notes 1"));
    }

    @Test
    public void getScannerShouldReturnAScanner() {
        // Given
        TestScanner scanner = new TestScanner("test-id-1", "Test Description 1", "Test Notes 1");
        // Had to use doReturn() syntax for Mockito due to getScanner method returning a generic type with a "wildcard capture"
        doReturn(scanner).when(mockScannerRegistry).getItem(scanner.id());

        // When
        Scanner returnValue = underTest.getScanner(scanner.id());

        // Then
        assertThat(returnValue).isEqualTo(new Scanner("test-id-1", "Test Description 1", "Test Notes 1"));
    }

    @Test
    public void getScannerShouldNotReturnAScannerWhenScannerIdIsUnknown() {
        // Given
        String scannerId = "unknown";
        when(mockScannerRegistry.getItem(scannerId)).thenReturn(null);

        // When
        Scanner returnValue = underTest.getScanner(scannerId);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getTestsShouldReturnAllTests() {
        // Given
        List<tech.kronicle.service.tests.Test<?>> tests = List.of(
                createTestTest("1", Priority.VERY_HIGH),
                createTestTest("2", Priority.HIGH),
                createTestTest("3", Priority.MEDIUM));
        when(mockTestFinder.getAllTests()).thenReturn(tests);

        // When
        List<tech.kronicle.sdk.models.Test> returnValue = underTest.getTests();

        // Then
        assertThat(returnValue).hasSize(3);
        tech.kronicle.sdk.models.Test test;
        test = returnValue.get(0);
        assertThat(test.getId()).isEqualTo("test-test-id-1");
        assertThat(test.getDescription()).isEqualTo("Test Description 1");
        assertThat(test.getPriority()).isEqualTo(Priority.VERY_HIGH);
        test = returnValue.get(1);
        assertThat(test.getId()).isEqualTo("test-test-id-2");
        assertThat(test.getDescription()).isEqualTo("Test Description 2");
        assertThat(test.getPriority()).isEqualTo(Priority.HIGH);
        test = returnValue.get(2);
        assertThat(test.getId()).isEqualTo("test-test-id-3");
        assertThat(test.getDescription()).isEqualTo("Test Description 3");
        assertThat(test.getPriority()).isEqualTo(Priority.MEDIUM);
    }

    @Test
    public void getTestsShouldReturnTestsSortedByName() {
        // Given
        List<tech.kronicle.service.tests.Test<?>> tests = List.of(
                createTestTest("a", Priority.VERY_HIGH),
                createTestTest("c", Priority.HIGH),
                createTestTest("b", Priority.MEDIUM));
        when(mockTestFinder.getAllTests()).thenReturn(tests);

        // When
        List<tech.kronicle.sdk.models.Test> returnValue = underTest.getTests();

        // Then
        assertThat(returnValue).hasSize(3);
        assertThat(returnValue.get(0).getId()).isEqualTo("test-test-id-a");
        assertThat(returnValue.get(1).getId()).isEqualTo("test-test-id-b");
        assertThat(returnValue.get(2).getId()).isEqualTo("test-test-id-c");
    }

    @Test
    public void getTestsShouldReturnAnUnmodifiableList() {
        // Given
        List<tech.kronicle.service.tests.Test<?>> tests = List.of(
                createTestTest("1", Priority.VERY_HIGH));
        when(mockTestFinder.getAllTests()).thenReturn(tests);

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTests().add(tech.kronicle.sdk.models.Test.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void getTestShouldReturnTheTestWithMatchingTestId() {
        // Given
        String testId = "test-test-id-2";
        doReturn(createTestTest("2", Priority.HIGH)).when(mockTestFinder).getTest(testId);

        // When
        tech.kronicle.sdk.models.Test test = underTest.getTest(testId);

        // Then
        assertThat(test).isNotNull();
        assertThat(test.getId()).isEqualTo("test-test-id-2");
        assertThat(test.getDescription()).isEqualTo("Test Description 2");
        assertThat(test.getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    public void getTestShouldNotReturnATestWhenTestIdIsUnknown() {
        // Given
        String testId = "unknown";
        when(mockTestFinder.getTest(testId)).thenReturn(null);

        // When
        tech.kronicle.sdk.models.Test returnValue = underTest.getTest(testId);

        // Then
        assertThat(returnValue).isNull();
    }

    public static Stream<Consumer<ComponentRepository>> provideInitializeAndRefreshScenarios() {
        return Stream.of(
                ComponentRepositoryTest::initializeAndWaitForRefreshToFinish,
                ComponentRepositoryTest::refreshAndWaitForFinish);
    }

    private static void initializeAndWaitForRefreshToFinish(ComponentRepository underTest) {
        runActionAndWaitForFinish(underTest::initialize);
    }

    private static void refreshAndWaitForFinish(ComponentRepository underTest) {
        runActionAndWaitForFinish(underTest::refresh);
    }

    private static void runActionAndWaitForFinish(Runnable action) {
        try (LogCaptor logCaptor = new LogCaptor(ComponentRepository.class)) {
            action.run();
            await().atMost(10, SECONDS).until(() -> eventWithThrowableHasBeenLogged(logCaptor)
                    || eventWithMessageHasBeenLogged(logCaptor, "Finished refresh"));
        }
    }

    private static boolean eventWithThrowableHasBeenLogged(LogCaptor logCaptor) {
        return logCaptor.getEvents().stream()
                .map(ILoggingEvent::getThrowableProxy)
                .anyMatch(Objects::nonNull);
    }

    private static boolean eventWithMessageHasBeenLogged(LogCaptor logCaptor, String message) {
        return logCaptor.getEvents().stream()
                .anyMatch(event -> Objects.equals(event.getMessage(), message));
    }

    private ComponentMetadata createComponentMetadata() {
        return ComponentMetadata
                .builder()
                .areas(List.of(Area.builder().id("test-area-id-1").name("Test Area Name 1").build(),
                        Area.builder().id("test-area-id-2").name("Test Area Name 2").build()))
                .teams(List.of(
                        Team.builder().id("test-team-id-1").name("Test Team Name 1").areaId("test-area-id-1").emailAddress("test-team-1@example.com").build(),
                        Team.builder().id("test-team-id-2").name("Test Team Name 2").areaId("test-area-id-2").emailAddress("test-team-2@example.com").build()))
                .components(List.of(
                        createComponentBuilder(1)
                                .teams(List.of(ComponentTeam.builder().teamId("test-team-id-1").build()))
                                .build(),
                        createComponentBuilder(2)
                                .teams(List.of(ComponentTeam.builder().teamId("test-team-id-2").build()))
                                .build()))
                .diagrams(List.of(
                        createDiagram(1),
                        createDiagram(2)
                ))
                .build();
    }

    private Map<String, Component> getComponentMapArgument(InvocationOnMock invocation) {
        return invocation.getArgument(1);
    }

    private Map<String, Diagram> getDiagramMapArgument(InvocationOnMock invocation) {
        return invocation.getArgument(2);
    }

    private Consumer<Summary> getSummaryConsumerArgument(InvocationOnMock invocation) {
        return invocation.getArgument(3);
    }

    private void assertDuringInitializeTheComponentsAndSummaryAreUpdatedStraightAway(
            Map<String, Component> componentMap,
            Map<String, Diagram> diagramMap,
            Consumer<Summary> summaryConsumer
    ) {
        assertThat(underTest.getSummary()).isEqualTo(Summary.EMPTY);
        componentMap.put(createComponentId(3), createComponent(3));
        diagramMap.put(createDiagramId(3), createDiagram(3));
        assertThat(underTest.getComponents()).hasSize(3);
        assertItemIds(ComponentRepository::getComponents, List.of(
                "test-component-id-1",
                "test-component-id-2",
                "test-component-id-3"
        ));
        assertItemIds(ComponentRepository::getDiagrams, List.of(
                "test-diagram-id-1",
                "test-diagram-id-2",
                "test-diagram-id-3"
        ));
        summaryConsumer.accept(createTestSummary("initialize-1"));
        assertThat(underTest.getSummary()).isEqualTo(createTestSummary("initialize-1"));
        componentMap.put(createComponentId(4), createComponent(4));
        diagramMap.put(createDiagramId(4), createDiagram(4));
        assertThat(underTest.getComponents()).hasSize(4);
        assertItemIds(ComponentRepository::getComponents, List.of(
                "test-component-id-1",
                "test-component-id-2",
                "test-component-id-3",
                "test-component-id-4"
        ));
        assertItemIds(ComponentRepository::getDiagrams, List.of(
                "test-diagram-id-1",
                "test-diagram-id-2",
                "test-diagram-id-3",
                "test-diagram-id-4"
        ));
        summaryConsumer.accept(createTestSummary("initialize-2"));
        assertThat(underTest.getSummary()).isEqualTo(createTestSummary("initialize-2"));
    }

    private void assertDuringRefreshTheComponentsAndSummaryAreUpdatedOnlyAtTheEnd(
            Map<String, Component> componentMap,
            Map<String, Diagram> diagramMap,
            Consumer<Summary> summaryConsumer
    ) {
        assertThat(underTest.getSummary()).isEqualTo(createTestSummary("initialize-2"));
        componentMap.put(createComponentId(7), createComponent(7));
        diagramMap.put(createDiagramId(7), createDiagram(7));
        assertItemIds(ComponentRepository::getComponents, List.of(
                "test-component-id-1",
                "test-component-id-2",
                "test-component-id-3",
                "test-component-id-4"
        ));
        summaryConsumer.accept(createTestSummary("refresh-1"));
        assertThat(underTest.getSummary()).isEqualTo(createTestSummary("initialize-2"));
        componentMap.put(createComponentId(8), createComponent(8));
        diagramMap.put(createDiagramId(8), createDiagram(8));
        assertItemIds(ComponentRepository::getDiagrams, List.of(
                "test-diagram-id-1",
                "test-diagram-id-2",
                "test-diagram-id-3",
                "test-diagram-id-4"
        ));
        summaryConsumer.accept(createTestSummary("refresh-2"));
        assertThat(underTest.getSummary()).isEqualTo(createTestSummary("initialize-2"));
    }

    private <T extends ObjectWithId> void assertItemIds(
            Function<ComponentRepository, List<T>> itemsGetter,
            List<String> expectedItemIds
    ) {
        List<T> items = itemsGetter.apply(underTest);
        List<String> itemIds = items.stream()
                .map(ObjectWithId::getId)
                .sorted()
                .collect(toUnmodifiableList());
        assertThat(itemIds).containsExactlyElementsOf(expectedItemIds);
    }

    private Summary createTestSummary(String missingComponentId) {
        return Summary.builder()
                .missingComponents(List.of(SummaryMissingComponent.builder().id(missingComponentId).build()))
                .build();
    }

    private TestTest createTestTest(String uniquePart, Priority priority) {
        return new TestTest(
                "test-test-id-" + uniquePart,
                "Test Description " + uniquePart,
                "Test Note " + uniquePart,
                priority);
    }

    @RequiredArgsConstructor
    private static class TestScanner extends tech.kronicle.pluginapi.scanners.Scanner<ObjectWithReference, Object> {

        private final String id;
        private final String description;
        private final String notes;

        @Override
        public String id() {
            return id;
        }

        @Override
        public String description() {
            return description;
        }

        @Override
        public String notes() {
            return notes;
        }

        @Override
        public Output<Object, Component> scan(ObjectWithReference input) {
            return null;
        }
    }

    @RequiredArgsConstructor
    private static class TestTest extends tech.kronicle.service.tests.Test<Component> {

        private final String id;
        private final String description;
        private final String notes;
        private final Priority priority;

        @Override
        public String id() {
            return id;
        }

        @Override
        public String description() {
            return description;
        }

        @Override
        public String notes() {
            return notes;
        }

        @Override
        public Priority priority() {
            return priority;
        }

        @Override
        public TestResult test(Component input, TestContext testContext) {
            return null;
        }
    }
}
