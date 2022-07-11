package tech.kronicle.service.services;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.models.ComponentsAndDiagrams;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.pluginapi.scanners.CodebaseScanner;
import tech.kronicle.pluginapi.scanners.ComponentAndCodebaseScanner;
import tech.kronicle.pluginapi.scanners.ComponentScanner;
import tech.kronicle.pluginapi.scanners.LateComponentScanner;
import tech.kronicle.pluginapi.scanners.RepoScanner;
import tech.kronicle.pluginapi.scanners.Scanner;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.ComponentAndCodebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.*;
import tech.kronicle.service.exceptions.ValidationException;
import tech.kronicle.tracingprocessor.internal.services.ComponentAliasResolver;
import tech.kronicle.tracingprocessor.GraphProcessor;
import tech.kronicle.utils.ThrowableToScannerErrorMapper;

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static tech.kronicle.sdk.utils.ListUtils.unmodifiableUnionOfLists;
import static tech.kronicle.service.testutils.DiagramUtils.createDiagram;
import static tech.kronicle.tracingprocessor.testutils.TestDataHelper.createTracingDataList;

@ExtendWith(MockitoExtension.class)
public class ScanEngineTest {

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private List<ScanLogEntry> scanLog;
    @Mock
    private MasterComponentFinder masterComponentFinder;
    @Mock
    private MasterTracingDataFinder masterTracingDataFinder;
    @Mock
    private MasterDiagramFinder masterDiagramFinder;
    @Mock
    private GraphProcessor graphProcessor;
    @Mock
    private ComponentAliasMapCreator componentAliasMapCreator;
    @Mock
    private ComponentAliasResolver componentAliasResolver;
    @Mock
    private ScannerExtensionRegistry scannerRegistry;
    @Mock
    private ValidatorService validatorService;
    private ScanEngine underTest;

    @BeforeEach
    public void beforeEach() {
        scanLog = new ArrayList<>();
        ThrowableToScannerErrorMapper throwableToScannerErrorMapper = new ThrowableToScannerErrorMapper();
        underTest = new ScanEngine(
                masterComponentFinder,
                masterTracingDataFinder,
                masterDiagramFinder,
                graphProcessor,
                componentAliasMapCreator,
                componentAliasResolver,
                scannerRegistry,
                new ExtensionExecutor(
                        new ExtensionOutputCache(
                                new ExtensionOutputCacheLoader(),
                                new ExtensionOutputCacheExpiry()
                        ),
                        throwableToScannerErrorMapper
                ),
                validatorService,
                throwableToScannerErrorMapper
        );
    }

    @Test
    public void scanShouldPassEachComponentThroughEachScannerAndTransformSummaryAndShouldAlwaysPassLatestVersionsOfComponentsAsInputForComponentBasedScanners() {
        // Given
        Component component1 = createComponent("1");
        Component component2 = createComponent("2");
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(component1, component2))
                .build();
        ComponentMetadata updatedComponentMetadata = mockMasterComponentFinder(componentMetadata);
        ComponentMetadata updatedComponentMetadata2 = mockMasterTracingDataFinderAndMasterDiagramFinder(updatedComponentMetadata);
        ConcurrentHashMap<String, Component> componentMap = createComponentMap(componentMetadata);
        ConcurrentHashMap<String, Diagram> diagramMap = createDiagramMap(componentMetadata);
        TestScannerConfig config = TestScannerConfig.builder().output(true).build();
        TestComponentScanner1 testComponentScanner1 = new TestComponentScanner1(config);
        TestComponentScanner2 testComponentScanner2 = new TestComponentScanner2(config);
        when(scannerRegistry.getComponentScanners()).thenReturn(List.of(testComponentScanner1, testComponentScanner2));
        TestRepoScanner testRepoScanner = new TestRepoScanner(config);
        when(scannerRegistry.getRepoScanner()).thenReturn(testRepoScanner);
        TestCodebaseScanner1 testCodebaseScanner1 = new TestCodebaseScanner1(config);
        TestCodebaseScanner2 testCodebaseScanner2 = new TestCodebaseScanner2(config);
        when(scannerRegistry.getCodebaseScanners()).thenReturn(List.of(testCodebaseScanner1, testCodebaseScanner2));
        TestComponentAndCodebaseScanner1 testComponentAndCodebaseScanner1 = new TestComponentAndCodebaseScanner1(config);
        TestComponentAndCodebaseScanner2 testComponentAndCodebaseScanner2 = new TestComponentAndCodebaseScanner2(config);
        when(scannerRegistry.getComponentAndCodebaseScanners()).thenReturn(List.of(testComponentAndCodebaseScanner1,
                testComponentAndCodebaseScanner2));
        TestLateComponentScanner1 testLateComponentScanner1 = new TestLateComponentScanner1(config);
        TestLateComponentScanner2 testLateComponentScanner2 = new TestLateComponentScanner2(config);
        when(scannerRegistry.getLateComponentScanners()).thenReturn(List.of(testLateComponentScanner1, testLateComponentScanner2));
        List<Summary> summaries = new ArrayList<>();
        SummaryMissingComponent missingComponent1 = createTestMissingComponent("TestComponentScanner1");
        SummaryMissingComponent missingComponent2 = createTestMissingComponent("TestComponentScanner2");
        SummaryMissingComponent missingComponent3 = createTestMissingComponent("TestRepoScanner");
        SummaryMissingComponent missingComponent4 = createTestMissingComponent("TestCodebaseScanner1");
        SummaryMissingComponent missingComponent5 = createTestMissingComponent("TestCodebaseScanner2");
        SummaryMissingComponent missingComponent6 = createTestMissingComponent("TestComponentAndCodebaseScanner1");
        SummaryMissingComponent missingComponent7 = createTestMissingComponent("TestComponentAndCodebaseScanner2");
        SummaryMissingComponent missingComponent8 = createTestMissingComponent("TestLateComponentScanner1");
        SummaryMissingComponent missingComponent9 = createTestMissingComponent("TestLateComponentScanner2");

        // When
        underTest.scan(componentMetadata, componentMap, diagramMap, summaries::add);

        // Then
        // Uses updatedComponentMetadata
        assertThat(testComponentScanner1.componentMetadataItems).containsExactlyInAnyOrder(updatedComponentMetadata);
        assertThat(testComponentScanner2.componentMetadataItems).containsExactlyInAnyOrder(updatedComponentMetadata);
        assertThat(testRepoScanner.componentMetadataItems).containsExactlyInAnyOrder(updatedComponentMetadata);
        assertThat(testCodebaseScanner1.componentMetadataItems).containsExactlyInAnyOrder(updatedComponentMetadata);
        assertThat(testCodebaseScanner2.componentMetadataItems).containsExactlyInAnyOrder(updatedComponentMetadata);
        assertThat(testComponentAndCodebaseScanner1.componentMetadataItems).containsExactlyInAnyOrder(updatedComponentMetadata);
        assertThat(testComponentAndCodebaseScanner2.componentMetadataItems).containsExactlyInAnyOrder(updatedComponentMetadata);

        // Uses updatedComponentMetadata2
        assertThat(testLateComponentScanner1.componentMetadataItems).containsExactlyInAnyOrder(updatedComponentMetadata2);
        assertThat(testLateComponentScanner2.componentMetadataItems).containsExactlyInAnyOrder(updatedComponentMetadata2);
        
        assertThat(componentMap.get("test-component-1").getTags()).containsExactlyInAnyOrder(
                createTag("input-test-component-1-has-0-tags"),
                createTag("input-test-component-1-has-1-tags"),
                createTag("input-test-component-1-has-2-tags"),
                createTag("input-test-component-1-has-3-tags"),
                createTag("input-test-component-1-has-4-tags"),
                createTag("input-test-component-1-has-5-tags")
        );
        assertThat(componentMap.get("test-component-1").getTechDebts()).containsExactlyInAnyOrder(
                createTestTechDebt("Update to test-component-1 from TestComponentScanner1"),
                createTestTechDebt("Update to test-component-1 from TestComponentScanner2"),
                createTestTechDebt("Update to test-component-1 from TestRepoScanner"),
                createTestTechDebt("Update to test-component-1 from TestCodebaseScanner1"),
                createTestTechDebt("Update to test-component-1 from TestCodebaseScanner2"),
                createTestTechDebt("Update to test-component-1 from TestComponentAndCodebaseScanner1"),
                createTestTechDebt("Update to test-component-1 from TestComponentAndCodebaseScanner2"),
                createTestTechDebt("Update to test-component-1 from TestLateComponentScanner1"),
                createTestTechDebt("Update to test-component-1 from TestLateComponentScanner2"));
        assertThat(componentMap.get("test-component-2").getTags()).containsExactlyInAnyOrder(
                createTag("input-test-component-2-has-0-tags"),
                createTag("input-test-component-2-has-1-tags"),
                createTag("input-test-component-2-has-2-tags"),
                createTag("input-test-component-2-has-3-tags"),
                createTag("input-test-component-2-has-4-tags"),
                createTag("input-test-component-2-has-5-tags")
        );
        assertThat(componentMap.get("test-component-2").getTechDebts()).containsExactlyInAnyOrder(
                createTestTechDebt("Update to test-component-2 from TestComponentScanner1"),
                createTestTechDebt("Update to test-component-2 from TestComponentScanner2"),
                createTestTechDebt("Update to test-component-2 from TestRepoScanner"),
                createTestTechDebt("Update to test-component-2 from TestCodebaseScanner1"),
                createTestTechDebt("Update to test-component-2 from TestCodebaseScanner2"),
                createTestTechDebt("Update to test-component-2 from TestComponentAndCodebaseScanner1"),
                createTestTechDebt("Update to test-component-2 from TestComponentAndCodebaseScanner2"),
                createTestTechDebt("Update to test-component-2 from TestLateComponentScanner1"),
                createTestTechDebt("Update to test-component-2 from TestLateComponentScanner2"));
        assertThat(summaries).containsExactly(
                createTestSummary(missingComponent1),
                createTestSummary(missingComponent1, missingComponent2),
                createTestSummary(missingComponent1, missingComponent2, missingComponent3),
                createTestSummary(missingComponent1, missingComponent2, missingComponent3, missingComponent4),
                createTestSummary(missingComponent1, missingComponent2, missingComponent3, missingComponent4, missingComponent5),
                createTestSummary(missingComponent1, missingComponent2, missingComponent3, missingComponent4, missingComponent5, missingComponent6),
                createTestSummary(missingComponent1, missingComponent2, missingComponent3, missingComponent4, missingComponent5, missingComponent6, missingComponent7),
                createTestSummary(missingComponent1, missingComponent2, missingComponent3, missingComponent4, missingComponent5, missingComponent6, missingComponent7, missingComponent8),
                createTestSummary(missingComponent1, missingComponent2, missingComponent3, missingComponent4, missingComponent5, missingComponent6, missingComponent7, missingComponent8, missingComponent9));
    }
    
    @Test
    public void scanShouldValidateTransformedComponents() {
        // Given
        Component component1 = createComponent("1");
        Component component2 = createComponent("2");
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(component1, component2))
                .build();
        ComponentMetadata updatedComponentMetadata = mockMasterComponentFinder(componentMetadata);
        mockMasterTracingDataFinderAndMasterDiagramFinder(updatedComponentMetadata);
        ConcurrentHashMap<String, Component> componentMap = createComponentMap(componentMetadata);
        ConcurrentHashMap<String, Diagram> diagramMap = createDiagramMap(componentMetadata);
        TestScannerConfig config = TestScannerConfig.builder().output(true).build();
        when(scannerRegistry.getComponentScanners()).thenReturn(List.of(new TestComponentScanner1(config), new TestComponentScanner2(config)));
        when(scannerRegistry.getRepoScanner()).thenReturn(new TestRepoScanner(config));
        when(scannerRegistry.getCodebaseScanners()).thenReturn(List.of(new TestCodebaseScanner1(config), new TestCodebaseScanner2(config)));
        doAnswer(answer -> {
            Component component = answer.getArgument(0);
            if (component.getTechDebts().size() > 0) {
                throw new ValidationException("Validation failure");
            }
            return null;
        }).when(validatorService).validate(any());

        // When
        underTest.scan(componentMetadata, componentMap, diagramMap, summary -> {});

        // Then
        List<ScannerError> scannerErrors;
        scannerErrors = componentMap.get("test-component-1").getScannerErrors();
        assertThat(scannerErrors).hasSize(5);
        scannerErrors.forEach(scannerError -> {
            assertThat(scannerError.getMessage()).isEqualTo("Validation failure for transformed component");
            assertThat(scannerError.getCause().getMessage()).isEqualTo("Validation failure");
        });
        assertThat(scannerErrors.get(0).getScannerId()).isEqualTo("TestComponentScanner1");
        assertThat(scannerErrors.get(1).getScannerId()).isEqualTo("TestComponentScanner2");
        assertThat(scannerErrors.get(2).getScannerId()).isEqualTo("TestRepoScanner");
        assertThat(scannerErrors.get(3).getScannerId()).isEqualTo("TestCodebaseScanner1");
        assertThat(scannerErrors.get(4).getScannerId()).isEqualTo("TestCodebaseScanner2");
        scannerErrors = componentMap.get("test-component-2").getScannerErrors();
        assertThat(scannerErrors).hasSize(5);
        scannerErrors.forEach(scannerError -> {
            assertThat(scannerError.getMessage()).isEqualTo("Validation failure for transformed component");
            assertThat(scannerError.getCause().getMessage()).isEqualTo("Validation failure");
        });
        assertThat(scannerErrors.get(0).getScannerId()).isEqualTo("TestComponentScanner1");
        assertThat(scannerErrors.get(1).getScannerId()).isEqualTo("TestComponentScanner2");
        assertThat(scannerErrors.get(2).getScannerId()).isEqualTo("TestRepoScanner");
        assertThat(scannerErrors.get(3).getScannerId()).isEqualTo("TestCodebaseScanner1");
        assertThat(scannerErrors.get(4).getScannerId()).isEqualTo("TestCodebaseScanner2");
    }

    @Test
    public void scanShouldCatchExceptionsThrowByScannerRefresh() {
        // Given
        Component component1 = createComponent("1");
        Component component2 = createComponent("2");
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(component1, component2))
                .build();
        ComponentMetadata updatedComponentMetadata = mockMasterComponentFinder(componentMetadata);
        mockMasterTracingDataFinderAndMasterDiagramFinder(updatedComponentMetadata);
        ConcurrentHashMap<String, Component> componentMap = createComponentMap(componentMetadata);
        ConcurrentHashMap<String, Diagram> diagramMap = createDiagramMap(componentMetadata);
        TestScannerConfig config = TestScannerConfig.builder().output(true).refreshException(true).build();
        when(scannerRegistry.getComponentScanners()).thenReturn(List.of(new TestComponentScanner1(config), new TestComponentScanner2(config)));
        when(scannerRegistry.getRepoScanner()).thenReturn(new TestRepoScanner(config));
        when(scannerRegistry.getCodebaseScanners()).thenReturn(List.of(new TestCodebaseScanner1(config), new TestCodebaseScanner2(config)));

        // When
        underTest.scan(componentMetadata, componentMap, diagramMap, summary -> {});

        // Then
        assertRefreshScannerErrors(componentMap.get("test-component-1"));
        assertRefreshScannerErrors(componentMap.get("test-component-2"));
    }

    @Test
    public void scanShouldCatchExceptionsThrowByScannerScan() {
        // Given
        Component component1 = createComponent("1");
        Component component2 = createComponent("2");
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(component1, component2))
                .build();
        ComponentMetadata updatedComponentMetadata = mockMasterComponentFinder(componentMetadata);
        mockMasterTracingDataFinderAndMasterDiagramFinder(updatedComponentMetadata);
        ConcurrentHashMap<String, Component> componentMap = createComponentMap(componentMetadata);
        ConcurrentHashMap<String, Diagram> diagramMap = createDiagramMap(componentMetadata);
        TestScannerConfig config = TestScannerConfig.builder().output(true).scanException(true).build();
        when(scannerRegistry.getComponentScanners()).thenReturn(List.of(new TestComponentScanner1(config), new TestComponentScanner2(config)));
        when(scannerRegistry.getRepoScanner()).thenReturn(new TestRepoScanner(config));
        when(scannerRegistry.getCodebaseScanners()).thenReturn(List.of(new TestCodebaseScanner1(config), new TestCodebaseScanner2(config)));

        // When
        underTest.scan(componentMetadata, componentMap, diagramMap, summary -> {});

        // Then
        assertScanExceptionScannerErrors(componentMap.get("test-component-1"));
        assertScanExceptionScannerErrors(componentMap.get("test-component-2"));
    }

    @Test
    public void scanShouldHandleScannerErrorsReturnedByScanner() {
        // Given
        Component component1 = createComponent("1");
        Component component2 = createComponent("2");
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(component1, component2))
                .build();
        ComponentMetadata updatedComponentMetadata = mockMasterComponentFinder(componentMetadata);
        mockMasterTracingDataFinderAndMasterDiagramFinder(updatedComponentMetadata);
        ConcurrentHashMap<String, Component> componentMap = createComponentMap(componentMetadata);
        ConcurrentHashMap<String, Diagram> diagramMap = createDiagramMap(componentMetadata);
        TestScannerConfig config = TestScannerConfig.builder().outputScannerError(true).build();
        when(scannerRegistry.getComponentScanners()).thenReturn(List.of(new TestComponentScanner1(config), new TestComponentScanner2(config)));
        when(scannerRegistry.getRepoScanner()).thenReturn(new TestRepoScanner(config));
        when(scannerRegistry.getCodebaseScanners()).thenReturn(List.of(new TestCodebaseScanner1(config), new TestCodebaseScanner2(config)));

        // When
        underTest.scan(componentMetadata, componentMap, diagramMap, summary -> {});

        // Then
        assertScanOutputScannerErrors(componentMap.get("test-component-1"), 3);
        assertScanOutputScannerErrors(componentMap.get("test-component-2"), 3);
    }

    @Test
    public void scanShouldHandleScannerErrorsAndOutputReturnedByScanner() {
        // Given
        Component component1 = createComponent("1");
        Component component2 = createComponent("2");
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(component1, component2))
                .build();
        ComponentMetadata updatedComponentMetadata = mockMasterComponentFinder(componentMetadata);
        mockMasterTracingDataFinderAndMasterDiagramFinder(updatedComponentMetadata);
        ConcurrentHashMap<String, Component> componentMap = createComponentMap(componentMetadata);
        ConcurrentHashMap<String, Diagram> diagramMap = createDiagramMap(componentMetadata);
        TestScannerConfig config = TestScannerConfig.builder().output(true).outputScannerError(true).build();
        when(scannerRegistry.getComponentScanners()).thenReturn(List.of(new TestComponentScanner1(config), new TestComponentScanner2(config)));
        when(scannerRegistry.getRepoScanner()).thenReturn(new TestRepoScanner(TestScannerConfig.builder().output(true).outputScannerError(true).build()));
        when(scannerRegistry.getCodebaseScanners()).thenReturn(List.of(new TestCodebaseScanner1(config), new TestCodebaseScanner2(config)));

        // When
        underTest.scan(componentMetadata, componentMap, diagramMap, summary -> {});

        // Then
        assertScanOutputScannerErrors(componentMap.get("test-component-1"), 5);
        assertScanOutputScannerErrors(componentMap.get("test-component-2"), 5);
    }

    @Test
    public void scanShouldScanInputsInAlphabeticalOrderSortedByReference() {
        // Given
        Component componentA = createComponent("a");
        Component componentB = createComponent("b");
        Component componentC = createComponent("c");
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(componentA, componentB, componentC))
                .build();
        ComponentMetadata updatedComponentMetadata = mockMasterComponentFinder(componentMetadata);
        mockMasterTracingDataFinderAndMasterDiagramFinder(updatedComponentMetadata);
        // Components are deliberately in the order b, a, c which is not sorted alphabetically
        ConcurrentHashMap<String, Component> componentMap = createComponentMap(componentMetadata);
        ConcurrentHashMap<String, Diagram> diagramMap = createDiagramMap(componentMetadata);
        TestScannerConfig config = TestScannerConfig.builder().output(true).outputScannerError(true).build();
        when(scannerRegistry.getComponentScanners()).thenReturn(List.of(new TestComponentScanner1(config), new TestComponentScanner2(config)));
        when(scannerRegistry.getRepoScanner()).thenReturn(new TestRepoScanner(TestScannerConfig.builder().output(true).outputScannerError(true).build()));
        when(scannerRegistry.getCodebaseScanners()).thenReturn(List.of(new TestCodebaseScanner1(config), new TestCodebaseScanner2(config)));
        when(scannerRegistry.getLateComponentScanners()).thenReturn(List.of(new TestLateComponentScanner1(config), new TestLateComponentScanner2(config)));

        // When
        underTest.scan(componentMetadata, componentMap, diagramMap, summary -> {});

        // Then
        assertThat(scanLog).containsExactly(
                new ScanLogEntry("TestComponentScanner1", "test-component-a"),
                new ScanLogEntry("TestComponentScanner1", "test-component-b"),
                new ScanLogEntry("TestComponentScanner1", "test-component-c"),
                new ScanLogEntry("TestComponentScanner1", "test-component-extra-1"),
                new ScanLogEntry("TestComponentScanner1", "test-component-extra-2"),
                new ScanLogEntry("TestComponentScanner2", "test-component-a"),
                new ScanLogEntry("TestComponentScanner2", "test-component-b"),
                new ScanLogEntry("TestComponentScanner2", "test-component-c"),
                new ScanLogEntry("TestComponentScanner2", "test-component-extra-1"),
                new ScanLogEntry("TestComponentScanner2", "test-component-extra-2"),
                new ScanLogEntry("TestRepoScanner", "test-repo-url-a"),
                new ScanLogEntry("TestRepoScanner", "test-repo-url-b"),
                new ScanLogEntry("TestRepoScanner", "test-repo-url-c"),
                new ScanLogEntry("TestCodebaseScanner1", "test-repo-url-a"),
                new ScanLogEntry("TestCodebaseScanner1", "test-repo-url-b"),
                new ScanLogEntry("TestCodebaseScanner1", "test-repo-url-c"),
                new ScanLogEntry("TestCodebaseScanner2", "test-repo-url-a"),
                new ScanLogEntry("TestCodebaseScanner2", "test-repo-url-b"),
                new ScanLogEntry("TestCodebaseScanner2", "test-repo-url-c"),
                new ScanLogEntry("TestLateComponentScanner1", "test-component-a"),
                new ScanLogEntry("TestLateComponentScanner1", "test-component-b"),
                new ScanLogEntry("TestLateComponentScanner1", "test-component-c"),
                new ScanLogEntry("TestLateComponentScanner1", "test-component-extra-1"),
                new ScanLogEntry("TestLateComponentScanner1", "test-component-extra-2"),
                new ScanLogEntry("TestLateComponentScanner2", "test-component-a"),
                new ScanLogEntry("TestLateComponentScanner2", "test-component-b"),
                new ScanLogEntry("TestLateComponentScanner2", "test-component-c"),
                new ScanLogEntry("TestLateComponentScanner2", "test-component-extra-1"),
                new ScanLogEntry("TestLateComponentScanner2", "test-component-extra-2")
        );
    }

    private ComponentMetadata mockMasterComponentFinder(ComponentMetadata componentMetadata) {
        Component component1 = createExtraComponent(1);
        Component component2 = createExtraComponent(2);
        String diagramIdSuffix = "master-component-finder";
        Diagram diagram1 = createExtraDiagram(diagramIdSuffix, 1);
        Diagram diagram2 = createExtraDiagram(diagramIdSuffix, 2);
        when(masterComponentFinder.findComponentsAndDiagrams(componentMetadata)).thenReturn(
                new ComponentsAndDiagrams(
                        List.of(
                                component1,
                                component2
                        ),
                        List.of(
                                diagram1,
                                diagram2
                        )
                )
        );

        Diagram processedDiagram1 = createProcessedDiagram(diagram1);
        Diagram processedDiagram2 = createProcessedDiagram(diagram2);
        when(graphProcessor.processDiagram(diagram1)).thenReturn(processedDiagram1);
        when(graphProcessor.processDiagram(diagram2)).thenReturn(processedDiagram2);
        List<Component> components = new ArrayList<>(componentMetadata.getComponents());
        components.add(component1);
        components.add(component2);
        List<Diagram> diagrams = new ArrayList<>(componentMetadata.getDiagrams());
        diagrams.add(processedDiagram1);
        diagrams.add(processedDiagram2);
        return componentMetadata.withComponents(components)
                .withDiagrams(diagrams);
    }

    private Component createComponent(String componentUniquePart) {
        return Component.builder()
                .id("test-component-" + componentUniquePart)
                .repo(new RepoReference("test-repo-url-" + componentUniquePart))
                .build();
    }

    private Component createExtraComponent(int componentNumber) {
        // Extra components have no repo
        return Component.builder()
                .id("test-component-extra-" + componentNumber)
                .build();
    }

    private Diagram createExtraDiagram(String diagramIdSuffix, int diagramNumber) {
        // Extra diagrams have no repo
        return Diagram.builder()
                .id("test-diagram-id-" + diagramIdSuffix + "-" + diagramNumber)
                .build();
    }

    private Diagram createProcessedDiagram(Diagram diagram1) {
        return diagram1.withDescription("processed");
    }

    private ComponentMetadata mockMasterTracingDataFinderAndMasterDiagramFinder(ComponentMetadata componentMetadata) {
        List<Diagram> processedTracingDataDiagrams = mockMasterTracingDataFinder(componentMetadata);
        List<Diagram> processedExtraDiagrams = mockMasterDiagramFinder(componentMetadata);
        return componentMetadata.withDiagrams(unmodifiableUnionOfLists(List.of(
                componentMetadata.getDiagrams(),
                processedTracingDataDiagrams,
                processedExtraDiagrams
        )));
    }

    private List<Diagram> mockMasterTracingDataFinder(ComponentMetadata componentMetadata) {
        List<TracingData> tracingDataList = createTracingDataList(1);
        when(masterTracingDataFinder.findTracingData(componentMetadata)).thenReturn(tracingDataList);
        Map<String, String> componentAliasMap = Map.ofEntries(
                Map.entry("alias-1", "component-id-1"),
                Map.entry("alias-2", "component-id-2")
        );
        when(componentAliasMapCreator.createComponentAliasMap(componentMetadata)).thenReturn(componentAliasMap);
        List<TracingData> updatedTracingDataList = createTracingDataList(2);
        when(componentAliasResolver.tracingDataList(tracingDataList, componentAliasMap)).thenReturn(updatedTracingDataList);
        String diagramIdSuffix = "master-tracing-data-finder";
        Diagram diagram1 = createExtraDiagram(diagramIdSuffix, 1);
        Diagram diagram2 = createExtraDiagram(diagramIdSuffix, 2);
        Diagram diagram3 = createExtraDiagram(diagramIdSuffix, 3);
        Diagram diagram4 = createExtraDiagram(diagramIdSuffix, 4);
        when(graphProcessor.processTracingData(updatedTracingDataList.get(0))).thenReturn(List.of(diagram1, diagram2));
        when(graphProcessor.processTracingData(updatedTracingDataList.get(1))).thenReturn(List.of(diagram3, diagram4));
        Diagram processedDiagram1 = createProcessedDiagram(diagram1);
        Diagram processedDiagram2 = createProcessedDiagram(diagram2);
        Diagram processedDiagram3 = createProcessedDiagram(diagram3);
        Diagram processedDiagram4 = createProcessedDiagram(diagram4);
        when(graphProcessor.processDiagram(diagram1)).thenReturn(processedDiagram1);
        when(graphProcessor.processDiagram(diagram2)).thenReturn(processedDiagram2);
        when(graphProcessor.processDiagram(diagram3)).thenReturn(processedDiagram3);
        when(graphProcessor.processDiagram(diagram4)).thenReturn(processedDiagram4);
        return List.of(
                processedDiagram1,
                processedDiagram2,
                processedDiagram3,
                processedDiagram4
        );
    }

    private List<Diagram> mockMasterDiagramFinder(ComponentMetadata componentMetadata) {
        String diagramIdSuffix = "master-diagram-finder";
        Diagram diagram1 = createExtraDiagram(diagramIdSuffix, 1);
        Diagram diagram2 = createExtraDiagram(diagramIdSuffix, 2);
        when(masterDiagramFinder.findDiagrams(componentMetadata)).thenReturn(List.of(
                diagram1,
                diagram2
        ));
        Diagram processedDiagram1 = createProcessedDiagram(diagram1);
        Diagram processedDiagram2 = createProcessedDiagram(diagram2);
        when(graphProcessor.processDiagram(diagram1)).thenReturn(processedDiagram1);
        when(graphProcessor.processDiagram(diagram2)).thenReturn(processedDiagram2);
        return List.of(
                processedDiagram1,
                processedDiagram2
        );
    }

    private void assertRefreshScannerErrors(Component component) {
        List<ScannerError> scannerErrors = component.getScannerErrors();
        // There will only be 3 errors as the RepoScanner failed to produce any codebases
        // for the CodebaseScanners to consume
        assertThat(scannerErrors).hasSize(3);
        scannerErrors.forEach(scannerError -> {
            assertThat(scannerError.getMessage()).isEqualTo("Failed to refresh scanner");
            assertThat(scannerError.getCause()).isNotNull();
            assertThat(scannerError.getCause().getMessage()).isEqualTo("Refresh failed");
            assertThat(scannerError.getCause().getCause()).isNull();
        });
        assertThat(scannerErrors.get(0).getScannerId()).isEqualTo("TestComponentScanner1");
        assertThat(scannerErrors.get(1).getScannerId()).isEqualTo("TestComponentScanner2");
        assertThat(scannerErrors.get(2).getScannerId()).isEqualTo("TestRepoScanner");
    }

    private void assertScanExceptionScannerErrors(Component component) {
        List<ScannerError> scannerErrors = component.getScannerErrors();
        // There will only be 3 errors as the RepoScanner failed to produce any codebases
        // for the CodebaseScanners to consume
        assertThat(scannerErrors).hasSize(3);
        scannerErrors.forEach(scannerError -> {
            String inputReference = null;
            if (scannerError.getScannerId().contains("Component")) {
                inputReference = component.reference();
            } else if (scannerError.getScannerId().contains("Repo")
                    || scannerError.getScannerId().contains("Codebase")) {
                inputReference = component.getRepo().reference();
            }
            assertThat(scannerError.getMessage()).isEqualTo(format("Failed to scan \"%s\"", inputReference));
            assertThat(scannerError.getCause()).isNotNull();
            assertThat(scannerError.getCause().getMessage()).isEqualTo("Scan exception");
            assertThat(scannerError.getCause().getCause()).isNull();
        });
        assertThat(scannerErrors.get(0).getScannerId()).isEqualTo("TestComponentScanner1");
        assertThat(scannerErrors.get(1).getScannerId()).isEqualTo("TestComponentScanner2");
        assertThat(scannerErrors.get(2).getScannerId()).isEqualTo("TestRepoScanner");
    }

    private void assertScanOutputScannerErrors(Component component, int scannerErrorCount) {
        List<ScannerError> scannerErrors = component.getScannerErrors();
        assertThat(scannerErrors).hasSize(scannerErrorCount);
        scannerErrors.forEach(scannerError -> {
            assertThat(scannerError.getMessage()).isEqualTo("Scan error");
            assertThat(scannerError.getCause()).isNull();
        });
        assertThat(scannerErrors.get(0).getScannerId()).isEqualTo("TestComponentScanner1");
        assertThat(scannerErrors.get(1).getScannerId()).isEqualTo("TestComponentScanner2");
        assertThat(scannerErrors.get(2).getScannerId()).isEqualTo("TestRepoScanner");
        if (scannerErrorCount > 3) {
            assertThat(scannerErrors.get(3).getScannerId()).isEqualTo("TestCodebaseScanner1");
            assertThat(scannerErrors.get(4).getScannerId()).isEqualTo("TestCodebaseScanner2");
        }
    }

    private ConcurrentHashMap<String, Component> createComponentMap(ComponentMetadata componentMetadata) {
        ConcurrentHashMap<String, Component> map = new ConcurrentHashMap<>();
        for (Component component : componentMetadata.getComponents()) {
            map.put(component.getId(), component);
        }
        return map;
    }
    
    private ConcurrentHashMap<String, Diagram> createDiagramMap(ComponentMetadata componentMetadata) {
        ConcurrentHashMap<String, Diagram> map = new ConcurrentHashMap<>();
        for (Diagram component : componentMetadata.getDiagrams()) {
            map.put(component.getId(), component);
        }
        return map;
    }

    private <I, O> Output<O, Component> createTestOutput(Scanner<?, ?> scanner, I input, O output) {
        return new Output<>(output, createTestComponentTransformer(scanner, input), null, CACHE_TTL);
    }

    private <I, O> Output<O, Component> createTestOutput(Scanner<?, ?> scanner, I input) {
        return new Output<>(null, createTestComponentTransformer(scanner, input), null, CACHE_TTL);
    }

    private <I, O> Output<O, Component> createTestOutput(Scanner<?, ?> scanner, I input, O output, ScannerError scannerError) {
        return new Output<>(output, createTestComponentTransformer(scanner, input), List.of(scannerError), CACHE_TTL);
    }

    private <I, O> Output<O, Component> createTestOutput(Scanner<?, ?> scanner, I input, ScannerError scannerError) {
        return new Output<>(null, createTestComponentTransformer(scanner, input), List.of(scannerError), CACHE_TTL);
    }

    private <I> UnaryOperator<Component> createTestComponentTransformer(Scanner<?, ?> scanner, I input) {
        return component -> {
            List<Tag> tags = new ArrayList<>(component.getTags());
            Component inputComponent;
            if (input instanceof Component) {
                inputComponent = (Component) input;
            } else if (input instanceof ComponentAndCodebase) {
                inputComponent = ((ComponentAndCodebase) input).getComponent();
            } else {
                inputComponent = null;
            }
            if (nonNull(inputComponent)) {
                tags.add(createTag(
                        format("input-%s-has-%d-tags", inputComponent.getId(), inputComponent.getTags().size())
                ));
            }
            List<TechDebt> techDebts = new ArrayList<>(component.getTechDebts());
            techDebts.add(createTestTechDebt("Update to " + component.getId() + " from " + scanner.id()));
            return component.withTags(tags)
                    .withTechDebts(techDebts);
        };
    }

    private Tag createTag(String key) {
        return Tag.builder()
                .key(key)
                .build();
    }

    private static void scannerRefresh(TestScannerConfig config, AtomicInteger refreshCount) {
        refreshCount.incrementAndGet();
        if (config.isRefreshException()) {
            throw new RuntimeException("Refresh failed");
        }
    }

    private <I extends ObjectWithReference, O> Output<O, Component> scannerScan(TestScannerConfig config, AtomicInteger refreshCount, Scanner<?, ?> scanner, I input, O output) {
        assertThat(refreshCount.get()).isGreaterThan(0);
        scanLog.add(new ScanLogEntry(scanner.id(), input.reference()));
        if (config.isScanException()) {
            throw new RuntimeException("Scan exception");
        }
        if (config.isOutputScannerError()) {
            ScannerError scannerError = new ScannerError(scanner.id(), "Scan error", null);
            if (nonNull(output)) {
                return createTestOutput(scanner, input, output, scannerError);
            } else {
                return createTestOutput(scanner, input, scannerError);
            }
        } else {
            if (nonNull(output)) {
                return createTestOutput(scanner, input, output);
            } else {
                return createTestOutput(scanner, input);
            }
        }
    }

    private <T> List<T> copyAndAddToList(List<T> list, T element) {
        List<T> newList = new ArrayList<>(list);
        newList.add(element);
        return newList;
    }

    private TechDebt createTestTechDebt(String description) {
        return TechDebt.builder()
                .description(description)
                .build();
    }

    private SummaryMissingComponent createTestMissingComponent(String id) {
        return SummaryMissingComponent.builder()
                .id(id)
                .build();
    }

    private Summary createTestSummary(SummaryMissingComponent... missingComponents) {
        return Summary.builder()
                .missingComponents(Arrays.asList(missingComponents))
                .build();
    }

    private Summary updateTestSummary(Summary summary, String componentId) {
        SummaryMissingComponent missingComponent = createTestMissingComponent(componentId);

        if (isNull(summary.getMissingComponents())) {
            return createTestSummary(missingComponent);
        }

        return summary.withMissingComponents(
                copyAndAddToList(summary.getMissingComponents(), missingComponent)
        );
    }

    @Value
    @Builder(toBuilder = true)
    private static class TestScannerConfig {
        boolean refreshException;
        boolean scanException;
        boolean outputScannerError;
        boolean output;
    }
    
    @RequiredArgsConstructor
    private class TestComponentScanner1 extends ComponentScanner {

        private final AtomicInteger refreshCount = new AtomicInteger();
        private final TestScannerConfig config;
        private final Set<ComponentMetadata> componentMetadataItems = new HashSet<>();

        @Override
        public void refresh(ComponentMetadata componentMetadata) {
            componentMetadataItems.add(componentMetadata);
            scannerRefresh(config, refreshCount);
        }

        @Override
        public String id() {
            return getClass().getSimpleName();
        }

        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<Void, Component> scan(Component input) {
            return scannerScan(config, refreshCount, this, input, null);
        }

        @Override
        public Summary transformSummary(Summary summary) {
            return updateTestSummary(summary, id());
        }
    }

    @RequiredArgsConstructor
    private class TestComponentScanner2 extends ComponentScanner {

        private final AtomicInteger refreshCount = new AtomicInteger();
        private final TestScannerConfig config;
        private final Set<ComponentMetadata> componentMetadataItems = new HashSet<>();

        @Override
        public void refresh(ComponentMetadata componentMetadata) {
            componentMetadataItems.add(componentMetadata);
            scannerRefresh(config, refreshCount);
        }

        @Override
        public String id() {
            return getClass().getSimpleName();
        }

        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<Void, Component> scan(Component input) {
            return scannerScan(config, refreshCount, this, input, null);
        }

        @Override
        public Summary transformSummary(Summary summary) {
            return updateTestSummary(summary, id());
        }
    }

    @RequiredArgsConstructor
    private class TestRepoScanner extends RepoScanner {

        private final AtomicInteger refreshCount = new AtomicInteger();
        private final TestScannerConfig config;
        private final Set<ComponentMetadata> componentMetadataItems = new HashSet<>();

        @Override
        public void refresh(ComponentMetadata componentMetadata) {
            componentMetadataItems.add(componentMetadata);
            scannerRefresh(config, refreshCount);
        }

        @Override
        public String id() {
            return getClass().getSimpleName();
        }

        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<Codebase, Component> scan(RepoReference input) {
            Codebase codebase = config.isOutput() ? new Codebase(input, Path.of(input.getUrl())) : null;
            return scannerScan(config, refreshCount, this, input, codebase);
        }

        @Override
        public Summary transformSummary(Summary summary) {
            return updateTestSummary(summary, id());
        }
    }

    @RequiredArgsConstructor
    private class TestCodebaseScanner1 extends CodebaseScanner {

        private final AtomicInteger refreshCount = new AtomicInteger();
        private final TestScannerConfig config;
        private final Set<ComponentMetadata> componentMetadataItems = new HashSet<>();

        @Override
        public void refresh(ComponentMetadata componentMetadata) {
            componentMetadataItems.add(componentMetadata);
            scannerRefresh(config, refreshCount);
        }

        @Override
        public String id() {
            return getClass().getSimpleName();
        }

        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<Void, Component> scan(Codebase input) {
            return scannerScan(config, refreshCount, this, input, null);
        }

        @Override
        public Summary transformSummary(Summary summary) {
            return updateTestSummary(summary, id());
        }
    }

    @RequiredArgsConstructor
    private class TestCodebaseScanner2 extends CodebaseScanner {

        private final AtomicInteger refreshCount = new AtomicInteger();
        private final TestScannerConfig config;
        private final Set<ComponentMetadata> componentMetadataItems = new HashSet<>();

        @Override
        public void refresh(ComponentMetadata componentMetadata) {
            componentMetadataItems.add(componentMetadata);
            scannerRefresh(config, refreshCount);
        }

        @Override
        public String id() {
            return getClass().getSimpleName();
        }

        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<Void, Component> scan(Codebase input) {
            return scannerScan(config, refreshCount, this, input, null);
        }

        @Override
        public Summary transformSummary(Summary summary) {
            return updateTestSummary(summary, id());
        }
    }

    @RequiredArgsConstructor
    private class TestComponentAndCodebaseScanner1 extends ComponentAndCodebaseScanner {

        private final AtomicInteger refreshCount = new AtomicInteger();
        private final TestScannerConfig config;
        private final Set<ComponentMetadata> componentMetadataItems = new HashSet<>();

        @Override
        public void refresh(ComponentMetadata componentMetadata) {
            componentMetadataItems.add(componentMetadata);
            scannerRefresh(config, refreshCount);
        }

        @Override
        public String id() {
            return getClass().getSimpleName();
        }

        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<Void, Component> scan(ComponentAndCodebase input) {
            return scannerScan(config, refreshCount, this, input, null);
        }

        @Override
        public Summary transformSummary(Summary summary) {
            return updateTestSummary(summary, id());
        }
    }

    @RequiredArgsConstructor
    private class TestComponentAndCodebaseScanner2 extends ComponentAndCodebaseScanner {

        private final AtomicInteger refreshCount = new AtomicInteger();
        private final TestScannerConfig config;
        private final Set<ComponentMetadata> componentMetadataItems = new HashSet<>();

        @Override
        public void refresh(ComponentMetadata componentMetadata) {
            componentMetadataItems.add(componentMetadata);
            scannerRefresh(config, refreshCount);
        }

        @Override
        public String id() {
            return getClass().getSimpleName();
        }

        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<Void, Component> scan(ComponentAndCodebase input) {
            return scannerScan(config, refreshCount, this, input, null);
        }

        @Override
        public Summary transformSummary(Summary summary) {
            return updateTestSummary(summary, id());
        }
    }

    @RequiredArgsConstructor
    private class TestLateComponentScanner1 extends LateComponentScanner {

        private final AtomicInteger refreshCount = new AtomicInteger();
        private final TestScannerConfig config;
        private final Set<ComponentMetadata> componentMetadataItems = new HashSet<>();

        @Override
        public void refresh(ComponentMetadata componentMetadata) {
            componentMetadataItems.add(componentMetadata);
            scannerRefresh(config, refreshCount);
        }

        @Override
        public String id() {
            return getClass().getSimpleName();
        }

        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<Void, Component> scan(Component input) {
            return scannerScan(config, refreshCount, this, input, null);
        }

        @Override
        public Summary transformSummary(Summary summary) {
            return updateTestSummary(summary, id());
        }
    }

    @RequiredArgsConstructor
    private class TestLateComponentScanner2 extends LateComponentScanner {

        private final AtomicInteger refreshCount = new AtomicInteger();
        private final TestScannerConfig config;
        private final Set<ComponentMetadata> componentMetadataItems = new HashSet<>();

        @Override
        public void refresh(ComponentMetadata componentMetadata) {
            componentMetadataItems.add(componentMetadata);
            scannerRefresh(config, refreshCount);
        }

        @Override
        public String id() {
            return getClass().getSimpleName();
        }

        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<Void, Component> scan(Component input) {
            return scannerScan(config, refreshCount, this, input, null);
        }

        @Override
        public Summary transformSummary(Summary summary) {
            return updateTestSummary(summary, id());
        }
    }

    @Value
    private static class ScanLogEntry {

        String scannerId;
        String inputReference;
    }
}
