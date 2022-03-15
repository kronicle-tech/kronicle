package tech.kronicle.service.services;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.pluginapi.scanners.CodebaseScanner;
import tech.kronicle.pluginapi.scanners.ComponentAndCodebaseScanner;
import tech.kronicle.pluginapi.scanners.ComponentScanner;
import tech.kronicle.pluginapi.scanners.LateComponentScanner;
import tech.kronicle.pluginapi.scanners.RepoScanner;
import tech.kronicle.pluginapi.scanners.Scanner;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.ComponentAndCodebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.utils.ThrowableToScannerErrorMapper;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.sdk.models.ObjectWithReference;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.sdk.models.ScannerError;
import tech.kronicle.sdk.models.Summary;
import tech.kronicle.sdk.models.SummaryComponentDependencies;
import tech.kronicle.sdk.models.SummaryComponentDependencyNode;
import tech.kronicle.sdk.models.TechDebt;
import tech.kronicle.service.exceptions.ValidationException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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

@ExtendWith(MockitoExtension.class)
public class ScanEngineTest {

    private List<ScanLogEntry> scanLog;
    @Mock
    private MasterComponentFinder masterComponentFinder;
    @Mock
    private MasterDependencyFinder masterDependencyFinder;
    @Mock
    private ScannerExtensionRegistry mockScannerRegistry;
    @Mock
    private ValidatorService mockValidatorService;
    private ScanEngine underTest;

    @BeforeEach
    public void beforeEach() {
        scanLog = new ArrayList<>();
        underTest = new ScanEngine(
                masterComponentFinder,
                masterDependencyFinder,
                mockScannerRegistry,
                mockValidatorService,
                new ThrowableToScannerErrorMapper()
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
        List<Dependency> dependencies = mockMasterDependencyFinder(updatedComponentMetadata);
        ConcurrentHashMap<String, Component> componentMap = createComponentMap(componentMetadata);
        TestScannerConfig config = TestScannerConfig.builder().output(true).build();
        TestComponentScanner1 testComponentScanner1 = new TestComponentScanner1(config);
        TestComponentScanner2 testComponentScanner2 = new TestComponentScanner2(config);
        when(mockScannerRegistry.getComponentScanners()).thenReturn(List.of(testComponentScanner1, testComponentScanner2));
        TestRepoScanner testRepoScanner = new TestRepoScanner(config);
        when(mockScannerRegistry.getRepoScanner()).thenReturn(testRepoScanner);
        TestCodebaseScanner1 testCodebaseScanner1 = new TestCodebaseScanner1(config);
        TestCodebaseScanner2 testCodebaseScanner2 = new TestCodebaseScanner2(config);
        when(mockScannerRegistry.getCodebaseScanners()).thenReturn(List.of(testCodebaseScanner1, testCodebaseScanner2));
        TestComponentAndCodebaseScanner1 testComponentAndCodebaseScanner1 = new TestComponentAndCodebaseScanner1(config);
        TestComponentAndCodebaseScanner2 testComponentAndCodebaseScanner2 = new TestComponentAndCodebaseScanner2(config);
        when(mockScannerRegistry.getComponentAndCodebaseScanners()).thenReturn(List.of(testComponentAndCodebaseScanner1,
                testComponentAndCodebaseScanner2));
        TestLateComponentScanner1 testLateComponentScanner1 = new TestLateComponentScanner1(config);
        TestLateComponentScanner2 testLateComponentScanner2 = new TestLateComponentScanner2(config);
        when(mockScannerRegistry.getLateComponentScanners()).thenReturn(List.of(testLateComponentScanner1, testLateComponentScanner2));
        List<Summary> summaries = new ArrayList<>();
        SummaryComponentDependencyNode node1 = new SummaryComponentDependencyNode("TestComponentScanner1");
        SummaryComponentDependencyNode node2 = new SummaryComponentDependencyNode("TestComponentScanner2");
        SummaryComponentDependencyNode node3 = new SummaryComponentDependencyNode("TestRepoScanner");
        SummaryComponentDependencyNode node4 = new SummaryComponentDependencyNode("TestCodebaseScanner1");
        SummaryComponentDependencyNode node5 = new SummaryComponentDependencyNode("TestCodebaseScanner2");
        SummaryComponentDependencyNode node6 = new SummaryComponentDependencyNode("TestComponentAndCodebaseScanner1");
        SummaryComponentDependencyNode node7 = new SummaryComponentDependencyNode("TestComponentAndCodebaseScanner2");
        SummaryComponentDependencyNode node8 = new SummaryComponentDependencyNode("TestLateComponentScanner1");
        SummaryComponentDependencyNode node9 = new SummaryComponentDependencyNode("TestLateComponentScanner2");

        // When
        underTest.scan(componentMetadata, componentMap, summaries::add);

        // Then
        assertThat(testComponentScanner1.componentMetadataItems).containsExactlyInAnyOrder(updatedComponentMetadata);
        assertThat(testComponentScanner2.componentMetadataItems).containsExactlyInAnyOrder(updatedComponentMetadata);
        assertThat(testRepoScanner.componentMetadataItems).containsExactlyInAnyOrder(updatedComponentMetadata);
        assertThat(testCodebaseScanner1.componentMetadataItems).containsExactlyInAnyOrder(updatedComponentMetadata);
        assertThat(testCodebaseScanner2.componentMetadataItems).containsExactlyInAnyOrder(updatedComponentMetadata);
        assertThat(testComponentAndCodebaseScanner1.componentMetadataItems).containsExactlyInAnyOrder(updatedComponentMetadata);
        assertThat(testComponentAndCodebaseScanner2.componentMetadataItems).containsExactlyInAnyOrder(updatedComponentMetadata);
        assertThat(testLateComponentScanner1.componentMetadataItems).containsExactlyInAnyOrder(updatedComponentMetadata);
        assertThat(testLateComponentScanner2.componentMetadataItems).containsExactlyInAnyOrder(updatedComponentMetadata);
        
        assertThat(testComponentScanner1.dependenciesItems).containsExactlyInAnyOrder(dependencies);
        assertThat(testComponentScanner2.dependenciesItems).containsExactlyInAnyOrder(dependencies);
        assertThat(testRepoScanner.dependenciesItems).containsExactlyInAnyOrder(dependencies);
        assertThat(testCodebaseScanner1.dependenciesItems).containsExactlyInAnyOrder(dependencies);
        assertThat(testCodebaseScanner2.dependenciesItems).containsExactlyInAnyOrder(dependencies);
        assertThat(testComponentAndCodebaseScanner1.dependenciesItems).containsExactlyInAnyOrder(dependencies);
        assertThat(testComponentAndCodebaseScanner2.dependenciesItems).containsExactlyInAnyOrder(dependencies);
        assertThat(testLateComponentScanner1.dependenciesItems).containsExactlyInAnyOrder(dependencies);
        assertThat(testLateComponentScanner2.dependenciesItems).containsExactlyInAnyOrder(dependencies);
        
        assertThat(componentMap.get("test-component-1").getTags()).containsExactlyInAnyOrder(
                "input-test-component-1-has-0-tags",
                "input-test-component-1-has-1-tags",
                "input-test-component-1-has-2-tags",
                "input-test-component-1-has-3-tags",
                "input-test-component-1-has-4-tags",
                "input-test-component-1-has-5-tags");
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
                "input-test-component-2-has-0-tags",
                "input-test-component-2-has-1-tags",
                "input-test-component-2-has-2-tags",
                "input-test-component-2-has-3-tags",
                "input-test-component-2-has-4-tags",
                "input-test-component-2-has-5-tags");
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
                createTestSummary(node1),
                createTestSummary(node1, node2),
                createTestSummary(node1, node2, node3),
                createTestSummary(node1, node2, node3, node4),
                createTestSummary(node1, node2, node3, node4, node5),
                createTestSummary(node1, node2, node3, node4, node5, node6),
                createTestSummary(node1, node2, node3, node4, node5, node6, node7),
                createTestSummary(node1, node2, node3, node4, node5, node6, node7, node8),
                createTestSummary(node1, node2, node3, node4, node5, node6, node7, node8, node9));
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
        mockMasterDependencyFinder(updatedComponentMetadata);
        ConcurrentHashMap<String, Component> componentMap = createComponentMap(componentMetadata);
        TestScannerConfig config = TestScannerConfig.builder().output(true).build();
        when(mockScannerRegistry.getComponentScanners()).thenReturn(List.of(new TestComponentScanner1(config), new TestComponentScanner2(config)));
        when(mockScannerRegistry.getRepoScanner()).thenReturn(new TestRepoScanner(config));
        when(mockScannerRegistry.getCodebaseScanners()).thenReturn(List.of(new TestCodebaseScanner1(config), new TestCodebaseScanner2(config)));
        doAnswer(answer -> {
            Component component = answer.getArgument(0);
            if (component.getTechDebts().size() > 0) {
                throw new ValidationException("Validation failure");
            }
            return null;
        }).when(mockValidatorService).validate(any());

        // When
        underTest.scan(componentMetadata, componentMap, summary -> {});

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
        mockMasterDependencyFinder(updatedComponentMetadata);
        ConcurrentHashMap<String, Component> componentMap = createComponentMap(componentMetadata);
        TestScannerConfig config = TestScannerConfig.builder().output(true).refreshException(true).build();
        when(mockScannerRegistry.getComponentScanners()).thenReturn(List.of(new TestComponentScanner1(config), new TestComponentScanner2(config)));
        when(mockScannerRegistry.getRepoScanner()).thenReturn(new TestRepoScanner(config));
        when(mockScannerRegistry.getCodebaseScanners()).thenReturn(List.of(new TestCodebaseScanner1(config), new TestCodebaseScanner2(config)));

        // When
        underTest.scan(componentMetadata, componentMap, summary -> {});

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
        mockMasterDependencyFinder(updatedComponentMetadata);
        ConcurrentHashMap<String, Component> componentMap = createComponentMap(componentMetadata);
        TestScannerConfig config = TestScannerConfig.builder().output(true).scanException(true).build();
        when(mockScannerRegistry.getComponentScanners()).thenReturn(List.of(new TestComponentScanner1(config), new TestComponentScanner2(config)));
        when(mockScannerRegistry.getRepoScanner()).thenReturn(new TestRepoScanner(config));
        when(mockScannerRegistry.getCodebaseScanners()).thenReturn(List.of(new TestCodebaseScanner1(config), new TestCodebaseScanner2(config)));

        // When
        underTest.scan(componentMetadata, componentMap, summary -> {});

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
        mockMasterDependencyFinder(updatedComponentMetadata);
        ConcurrentHashMap<String, Component> componentMap = createComponentMap(componentMetadata);
        TestScannerConfig config = TestScannerConfig.builder().outputScannerError(true).build();
        when(mockScannerRegistry.getComponentScanners()).thenReturn(List.of(new TestComponentScanner1(config), new TestComponentScanner2(config)));
        when(mockScannerRegistry.getRepoScanner()).thenReturn(new TestRepoScanner(config));
        when(mockScannerRegistry.getCodebaseScanners()).thenReturn(List.of(new TestCodebaseScanner1(config), new TestCodebaseScanner2(config)));

        // When
        underTest.scan(componentMetadata, componentMap, summary -> {});

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
        mockMasterDependencyFinder(updatedComponentMetadata);
        ConcurrentHashMap<String, Component> componentMap = createComponentMap(componentMetadata);
        TestScannerConfig config = TestScannerConfig.builder().output(true).outputScannerError(true).build();
        when(mockScannerRegistry.getComponentScanners()).thenReturn(List.of(new TestComponentScanner1(config), new TestComponentScanner2(config)));
        when(mockScannerRegistry.getRepoScanner()).thenReturn(new TestRepoScanner(TestScannerConfig.builder().output(true).outputScannerError(true).build()));
        when(mockScannerRegistry.getCodebaseScanners()).thenReturn(List.of(new TestCodebaseScanner1(config), new TestCodebaseScanner2(config)));

        // When
        underTest.scan(componentMetadata, componentMap, summary -> {});

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
        mockMasterDependencyFinder(updatedComponentMetadata);
        // Components are deliberately in the order b, a, c which is not sorted alphabetically
        ConcurrentHashMap<String, Component> componentMap = createComponentMap(componentMetadata);
        TestScannerConfig config = TestScannerConfig.builder().output(true).outputScannerError(true).build();
        when(mockScannerRegistry.getComponentScanners()).thenReturn(List.of(new TestComponentScanner1(config), new TestComponentScanner2(config)));
        when(mockScannerRegistry.getRepoScanner()).thenReturn(new TestRepoScanner(TestScannerConfig.builder().output(true).outputScannerError(true).build()));
        when(mockScannerRegistry.getCodebaseScanners()).thenReturn(List.of(new TestCodebaseScanner1(config), new TestCodebaseScanner2(config)));
        when(mockScannerRegistry.getLateComponentScanners()).thenReturn(List.of(new TestLateComponentScanner1(config), new TestLateComponentScanner2(config)));

        // When
        underTest.scan(componentMetadata, componentMap, summary -> {});

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
        Component extraComponent1 = createExtraComponent("1");
        Component extraComponent2 = createExtraComponent("2");
        when(masterComponentFinder.findComponents(componentMetadata)).thenReturn(List.of(
                extraComponent1,
                extraComponent2
        ));
        List<Component> components = new ArrayList<>(componentMetadata.getComponents());
        components.add(extraComponent1);
        components.add(extraComponent2);
        return componentMetadata.withComponents(components);
    }

    private Component createExtraComponent(String componentUniquePart) {
        // Extra components have no repo
        return Component.builder()
                .id("test-component-extra-" + componentUniquePart)
                .build();
    }

    private Component createComponent(String componentUniquePart) {
        return Component.builder()
                .id("test-component-" + componentUniquePart)
                .repo(new Repo("test-repo-url-" + componentUniquePart))
                .build();
    }
    
    private List<Dependency> mockMasterDependencyFinder(ComponentMetadata componentMetadata) {
        List<Dependency> dependencies = List.of(
                new Dependency("test-component-3", "test-component-4"),
                new Dependency("test-component-5", "test-component-6")
        );
        when(masterDependencyFinder.findDependencies(componentMetadata)).thenReturn(dependencies);
        return dependencies;
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

    private <I, O> Output<O> createTestOutput(Scanner<?, ?> scanner, I input, O output) {
        return Output.of(createTestComponentTransformer(scanner, input), output);
    }

    private <I, O> Output<O> createTestOutput(Scanner<?, ?> scanner, I input) {
        return Output.of(createTestComponentTransformer(scanner, input));
    }

    private <I, O> Output<O> createTestOutput(Scanner<?, ?> scanner, I input, O output, ScannerError scannerError) {
        return Output.of(createTestComponentTransformer(scanner, input), output, scannerError);
    }

    private <I, O> Output<O> createTestOutput(Scanner<?, ?> scanner, I input, ScannerError scannerError) {
        return Output.of(createTestComponentTransformer(scanner, input), scannerError);
    }

    private <I> UnaryOperator<Component> createTestComponentTransformer(Scanner<?, ?> scanner, I input) {
        return component -> {
            List<String> tags = new ArrayList<>(component.getTags());
            Component inputComponent;
            if (input instanceof Component) {
                inputComponent = (Component) input;
            } else if (input instanceof ComponentAndCodebase) {
                inputComponent = ((ComponentAndCodebase) input).getComponent();
            } else {
                inputComponent = null;
            }
            if (nonNull(inputComponent)) {
                tags.add(format("input-%s-has-%d-tags", inputComponent.getId(), inputComponent.getTags().size()));
            }
            List<TechDebt> techDebts = new ArrayList<>(component.getTechDebts());
            techDebts.add(createTestTechDebt("Update to " + component.getId() + " from " + scanner.id()));
            return component.withTags(tags)
                    .withTechDebts(techDebts);
        };
    }

    private static void scannerRefresh(TestScannerConfig config, AtomicInteger refreshCount) {
        refreshCount.incrementAndGet();
        if (config.isRefreshException()) {
            throw new RuntimeException("Refresh failed");
        }
    }

    private <I extends ObjectWithReference, O> Output<O> scannerScan(TestScannerConfig config, AtomicInteger refreshCount, Scanner<?, ?> scanner, I input, O output) {
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

    private Summary createTestSummary(SummaryComponentDependencyNode... nodes) {
        return Summary.builder().componentDependencies(new SummaryComponentDependencies(Arrays.asList(nodes), null)).build();
    }

    private Summary updateTestSummary(Summary summary, String componentId) {
        SummaryComponentDependencyNode node = new SummaryComponentDependencyNode(componentId);

        if (isNull(summary.getComponentDependencies())) {
            return createTestSummary(node);
        }

        return summary.withComponentDependencies(summary.getComponentDependencies().withNodes(copyAndAddToList(summary.getComponentDependencies().getNodes(),
                node)));
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
        private final Set<List<Dependency>> dependenciesItems = new HashSet<>();

        @Override
        public void refresh(ComponentMetadata componentMetadata, List<Dependency> dependencies) {
            componentMetadataItems.add(componentMetadata);
            dependenciesItems.add(dependencies);
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
        public Output<Void> scan(Component input) {
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
        private final Set<List<Dependency>> dependenciesItems = new HashSet<>();

        @Override
        public void refresh(ComponentMetadata componentMetadata, List<Dependency> dependencies) {
            componentMetadataItems.add(componentMetadata);
            dependenciesItems.add(dependencies);
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
        public Output<Void> scan(Component input) {
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
        private final Set<List<Dependency>> dependenciesItems = new HashSet<>();

        @Override
        public void refresh(ComponentMetadata componentMetadata, List<Dependency> dependencies) {
            componentMetadataItems.add(componentMetadata);
            dependenciesItems.add(dependencies);
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
        public Output<Codebase> scan(Repo input) {
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
        private final Set<List<Dependency>> dependenciesItems = new HashSet<>();

        @Override
        public void refresh(ComponentMetadata componentMetadata, List<Dependency> dependencies) {
            componentMetadataItems.add(componentMetadata);
            dependenciesItems.add(dependencies);
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
        public Output<Void> scan(Codebase input) {
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
        private final Set<List<Dependency>> dependenciesItems = new HashSet<>();

        @Override
        public void refresh(ComponentMetadata componentMetadata, List<Dependency> dependencies) {
            componentMetadataItems.add(componentMetadata);
            dependenciesItems.add(dependencies);
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
        public Output<Void> scan(Codebase input) {
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
        private final Set<List<Dependency>> dependenciesItems = new HashSet<>();

        @Override
        public void refresh(ComponentMetadata componentMetadata, List<Dependency> dependencies) {
            componentMetadataItems.add(componentMetadata);
            dependenciesItems.add(dependencies);
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
        public Output<Void> scan(ComponentAndCodebase input) {
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
        private final Set<List<Dependency>> dependenciesItems = new HashSet<>();

        @Override
        public void refresh(ComponentMetadata componentMetadata, List<Dependency> dependencies) {
            componentMetadataItems.add(componentMetadata);
            dependenciesItems.add(dependencies);
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
        public Output<Void> scan(ComponentAndCodebase input) {
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
        private final Set<List<Dependency>> dependenciesItems = new HashSet<>();

        @Override
        public void refresh(ComponentMetadata componentMetadata, List<Dependency> dependencies) {
            componentMetadataItems.add(componentMetadata);
            dependenciesItems.add(dependencies);
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
        public Output<Void> scan(Component input) {
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
        private final Set<List<Dependency>> dependenciesItems = new HashSet<>();

        @Override
        public void refresh(ComponentMetadata componentMetadata, List<Dependency> dependencies) {
            componentMetadataItems.add(componentMetadata);
            dependenciesItems.add(dependencies);
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
        public Output<Void> scan(Component input) {
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
