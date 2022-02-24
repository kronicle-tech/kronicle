package tech.kronicle.plugins.zipkin;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import tech.kronicle.componentmetadata.models.ComponentMetadata;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.sdk.models.Summary;
import tech.kronicle.sdk.models.SummaryCallGraph;
import tech.kronicle.sdk.models.SummaryComponentDependency;
import tech.kronicle.sdk.models.SummaryComponentDependencyDuration;
import tech.kronicle.sdk.models.SummaryComponentDependencyNode;
import tech.kronicle.sdk.models.SummarySubComponentDependencyNode;
import tech.kronicle.sdk.models.zipkin.Zipkin;
import tech.kronicle.sdk.models.zipkin.ZipkinDependency;
import tech.kronicle.plugintestutils.scanners.BaseScannerTest;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.zipkin.client.ZipkinClient;
import tech.kronicle.plugins.zipkin.config.ZipkinConfig;
import tech.kronicle.plugins.zipkin.services.CallGraphCollator;
import tech.kronicle.plugins.zipkin.services.ComponentDependencyCollator;
import tech.kronicle.plugins.zipkin.services.DependencyDurationCalculator;
import tech.kronicle.plugins.zipkin.services.DependencyHelper;
import tech.kronicle.plugins.zipkin.services.GenericDependencyCollator;
import tech.kronicle.plugins.zipkin.services.SubComponentDependencyCollator;
import tech.kronicle.plugins.zipkin.services.SubComponentDependencyTagFilter;
import tech.kronicle.plugins.zipkin.services.ZipkinService;
import tech.kronicle.plugins.zipkin.spring.ZipkinConfiguration;
import tech.kronicle.pluginutils.services.MapComparator;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class ZipkinScannerTest extends BaseScannerTest {

    private static final int PORT = 36206;
    private static final LocalDateTime TIMESTAMP = LocalDateTime.of(2021, 1, 1, 0, 0);
    private static final SummaryComponentDependencyDuration DURATION = new SummaryComponentDependencyDuration(123L, 123L, 123L, 123L, 123L, 123L);

    private final ZipkinWireMockFactory zipkinWireMockFactory = new ZipkinWireMockFactory();
    private final RetryConfig retryConfig = RetryConfig.custom()
            .maxAttempts(5)
            .waitDuration(Duration.ofMillis(1))
            .build();
    private final RetryRegistry retryRegistry = RetryRegistry.custom()
            .addRetryConfig("zipkin-client", retryConfig)
            .build();
    private ZipkinScanner underTest;
    private WireMockServer wireMockServer;
    private ZipkinService zipkinService;

    @AfterEach
    public void afterEach() {
        if (nonNull(wireMockServer)) {
            wireMockServer.stop();
        }
    }

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // Given
        createZipkinScanner(true);

        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("zipkin");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // Given
        createZipkinScanner(true);

        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Scans a Zipkin Server instance using its Zipkin API endpoints");
    }

    @Test
    public void notesShouldReturnTheNotesForTheScanner() {
        // Given
        createZipkinScanner(true);

        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isEqualTo("The scanner will retrieve all the names of the services in Zipkin and the names of all the spans belonging to those "
                + "services.  It will retrieve a sample of traces for each of the combinations of service name and span name and then uses the combination of "
                + "all those traces "
                + "to work out:\n"
                + "\n"
                + "* All the dependencies between the services\n"
                + "* Calculate the response time percentices for each service and span name combination");
    }

    @Test
    public void refreshShouldSucceed() {
        // Given
        createZipkinScanner(true);

        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();

        // When
        // No exception should be raised
        underTest.refresh(componentMetadata, List.of());
    }

    @Test
    public void scanShouldReturnUpstreamAndDownstreamDependenciesOfAService() {
        // Given
        wireMockServer = zipkinWireMockFactory.createWithRealResponses(PORT);
        createZipkinScanner(true);
        Component component = Component.builder()
                .id("test-service-1")
                .build();
        ComponentMetadata componentMetadata = ComponentMetadata.builder().components(List.of(component)).build();
        underTest.refresh(componentMetadata, List.of());

        // When
        Output<Void> returnValue = underTest.scan(component);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        Zipkin zipkin = getMutatedComponent(returnValue).getZipkin();
        assertThat(zipkin.getServiceName()).isEqualTo("test-service-1");
        assertThat(zipkin.getUsed()).isTrue();
        assertThat(zipkin.getUpstream()).containsExactly(
                new ZipkinDependency("test-upstream-service-1", "test-service-1", 10, 11),
                new ZipkinDependency("test-upstream-service-2", "test-service-1", 12, 13));
        assertThat(zipkin.getDownstream()).containsExactly(
                new ZipkinDependency("test-service-1", "test-downstream-service-1", 20, 21),
                new ZipkinDependency("test-service-1", "test-downstream-service-2", 22, 23));
    }

    @Test
    public void scanShouldReturnUsesFalseIfAServiceIsNotKnownToZipkin() {
        // Given
        wireMockServer = zipkinWireMockFactory.createWithRealResponses(PORT);
        createZipkinScanner(true);
        Component component = Component.builder()
                .id("unknown-service")
                .build();
        ComponentMetadata componentMetadata = ComponentMetadata.builder().components(List.of(component)).build();
        underTest.refresh(componentMetadata, List.of());

        // When
        Output<Void> returnValue = underTest.scan(component);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        Zipkin zipkin = getMutatedComponent(returnValue).getZipkin();
        assertThat(zipkin.getServiceName()).isEqualTo("unknown-service");
        assertThat(zipkin.getUsed()).isFalse();
        assertThat(zipkin.getUpstream()).isEmpty();
        assertThat(zipkin.getDownstream()).isEmpty();
    }

    @Test
    public void scanShouldTransformSummaryWithComponentDependencies() {
        // Given
        wireMockServer = zipkinWireMockFactory.createWithRealResponses(PORT);
        createZipkinScanner(true);
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        underTest.refresh(componentMetadata, List.of());
        Summary summary = Summary.EMPTY;

        // When
        Summary returnValue = underTest.transformSummary(summary);

        // Then
        assertThat(returnValue.getComponentDependencies()).isNotNull();
        assertThat(returnValue.getComponentDependencies().getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-1b"),
                new SummaryComponentDependencyNode("test-service-1c"),
                new SummaryComponentDependencyNode("test-service-2"),
                new SummaryComponentDependencyNode("test-service-2b"),
                new SummaryComponentDependencyNode("test-service-2c"));
        assertThat(returnValue.getComponentDependencies().getDependencies()).containsExactly(
                new SummaryComponentDependency(0, 1, List.of(), false, 2, TIMESTAMP, TIMESTAMP, DURATION),
                new SummaryComponentDependency(2, 0, List.of(), false, 2, TIMESTAMP, TIMESTAMP, DURATION),
                new SummaryComponentDependency(3, 4, List.of(), false, 2, TIMESTAMP, TIMESTAMP, DURATION),
                new SummaryComponentDependency(5, 3, List.of(), false, 2, TIMESTAMP, TIMESTAMP, DURATION));
    }

    @Test
    public void scanShouldReturnManualDependencies() {
        // Given
        wireMockServer = zipkinWireMockFactory.createWithRealResponses(PORT);
        createZipkinScanner(true);
        Component component = Component.builder()
                .id("test-unknown-service-1")
                .build();
        ComponentMetadata componentMetadata = ComponentMetadata.builder().components(List.of(component)).build();
        List<Dependency> dependencies = List.of(
                new Dependency("test-unknown-service-1", "test-unknown-service-2"),
                new Dependency("test-unknown-service-3", "test-unknown-service-1")
        );
        underTest.refresh(componentMetadata, dependencies);
        Summary summary = Summary.EMPTY;

        // When
        Summary returnValue = underTest.transformSummary(summary);

        // Then
        assertThat(returnValue.getComponentDependencies()).isNotNull();
        assertThat(returnValue.getComponentDependencies().getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-1b"),
                new SummaryComponentDependencyNode("test-service-1c"),
                new SummaryComponentDependencyNode("test-service-2"),
                new SummaryComponentDependencyNode("test-service-2b"),
                new SummaryComponentDependencyNode("test-service-2c"),
                new SummaryComponentDependencyNode("test-unknown-service-1"),
                new SummaryComponentDependencyNode("test-unknown-service-2"),
                new SummaryComponentDependencyNode("test-unknown-service-3"));
        assertThat(returnValue.getComponentDependencies().getDependencies()).containsExactly(
                new SummaryComponentDependency(0, 1, List.of(), false, 2, TIMESTAMP, TIMESTAMP, DURATION),
                new SummaryComponentDependency(2, 0, List.of(), false, 2, TIMESTAMP, TIMESTAMP, DURATION),
                new SummaryComponentDependency(3, 4, List.of(), false, 2, TIMESTAMP, TIMESTAMP, DURATION),
                new SummaryComponentDependency(5, 3, List.of(), false, 2, TIMESTAMP, TIMESTAMP, DURATION),
                new SummaryComponentDependency(6, 7, List.of(), true, 0, null, null, null),
                new SummaryComponentDependency(8, 6, List.of(), true, 0, null, null, null));
    }

    @Test
    public void scanShouldStillReturnManualDependenciesIfACallToZipkinServerFails() {
        // Given
        wireMockServer = zipkinWireMockFactory.createWithErrorResponses(PORT);
        createZipkinScanner(true);
        Component component = Component.builder()
                .id("test-unknown-service-1")
                .build();
        ComponentMetadata componentMetadata = ComponentMetadata.builder().components(List.of(component)).build();
        List<Dependency> dependencies = List.of(
                new Dependency("test-unknown-service-1", "test-unknown-service-2"),
                new Dependency("test-unknown-service-3", "test-unknown-service-1")
        );
        underTest.refresh(componentMetadata, dependencies);
        Summary summary = Summary.EMPTY;

        // When
        Summary returnValue = underTest.transformSummary(summary);

        // Then
        assertThat(returnValue.getComponentDependencies()).isNotNull();
        assertThat(returnValue.getComponentDependencies().getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-unknown-service-1"),
                new SummaryComponentDependencyNode("test-unknown-service-2"),
                new SummaryComponentDependencyNode("test-unknown-service-3"));
        assertThat(returnValue.getComponentDependencies().getDependencies()).containsExactly(
                new SummaryComponentDependency(0, 1, List.of(), true, 0, null, null, null),
                new SummaryComponentDependency(2, 0, List.of(), true, 0, null, null, null));
    }

    @Test
    public void scanShouldStillReturnManualDependenciesIfZipkinIsDisabled() {
        // Given
        createZipkinScanner(false);
        Component component = Component.builder()
                .id("test-unknown-service-1")
                .build();
        ComponentMetadata componentMetadata = ComponentMetadata.builder().components(List.of(component)).build();
        List<Dependency> dependencies = List.of(
                new Dependency("test-unknown-service-1", "test-unknown-service-2"),
                new Dependency("test-unknown-service-3", "test-unknown-service-1")
        );
        underTest.refresh(componentMetadata, dependencies);
        Summary summary = Summary.EMPTY;

        // When
        Summary returnValue = underTest.transformSummary(summary);

        // Then
        verifyNoInteractions(zipkinService);
        assertThat(returnValue.getComponentDependencies()).isNotNull();
        assertThat(returnValue.getComponentDependencies().getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-unknown-service-1"),
                new SummaryComponentDependencyNode("test-unknown-service-2"),
                new SummaryComponentDependencyNode("test-unknown-service-3"));
        assertThat(returnValue.getComponentDependencies().getDependencies()).containsExactly(
                new SummaryComponentDependency(0, 1, List.of(), true, 0, null, null, null),
                new SummaryComponentDependency(2, 0, List.of(), true, 0, null, null, null));
    }

    @Test
    public void scanShouldTransformSummaryWithSubComponentDependencies() {
        // Given
        wireMockServer = zipkinWireMockFactory.createWithRealResponses(PORT);
        createZipkinScanner(true);
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        underTest.refresh(componentMetadata, List.of());
        Summary summary = Summary.EMPTY;

        // When
        Summary returnValue = underTest.transformSummary(summary);

        // Then
        assertThat(returnValue.getSubComponentDependencies().getNodes()).containsExactly(
                new SummarySubComponentDependencyNode("test-service-1", "test-service-1-span-1", Map.of("http.path_template", "/test-service-1-span-1")),
                new SummarySubComponentDependencyNode("test-service-1", "test-service-1-span-2", Map.of("http.path_template", "/test-service-1-span-2")),
                new SummarySubComponentDependencyNode("test-service-1b", "test-service-1b-span-1b", Map.of("http.path_template", "/test-service-1b-span-1b")),
                new SummarySubComponentDependencyNode("test-service-1b", "test-service-1b-span-2b", Map.of("http.path_template", "/test-service-1b-span-2b")),
                new SummarySubComponentDependencyNode("test-service-1c", "test-service-1c-span-1c", Map.of("http.path_template", "/test-service-1c-span-1c")),
                new SummarySubComponentDependencyNode("test-service-1c", "test-service-1c-span-2c", Map.of("http.path_template", "/test-service-1c-span-2c")),
                new SummarySubComponentDependencyNode("test-service-2", "test-service-2-span-1", Map.of("http.path_template", "/test-service-2-span-1")),
                new SummarySubComponentDependencyNode("test-service-2", "test-service-2-span-2", Map.of("http.path_template", "/test-service-2-span-2")),
                new SummarySubComponentDependencyNode("test-service-2b", "test-service-2b-span-1b", Map.of("http.path_template", "/test-service-2b-span-1b")),
                new SummarySubComponentDependencyNode("test-service-2b", "test-service-2b-span-2b", Map.of("http.path_template", "/test-service-2b-span-2b")),
                new SummarySubComponentDependencyNode("test-service-2c", "test-service-2c-span-1c", Map.of("http.path_template", "/test-service-2c-span-1c")),
                new SummarySubComponentDependencyNode("test-service-2c", "test-service-2c-span-2c", Map.of("http.path_template", "/test-service-2c-span-2c")));
        assertThat(returnValue.getSubComponentDependencies().getDependencies()).containsExactly(
                new SummaryComponentDependency(0, 2, List.of(), false, 1, TIMESTAMP, TIMESTAMP, DURATION),
                new SummaryComponentDependency(1, 3, List.of(), false, 1, TIMESTAMP, TIMESTAMP, DURATION),
                new SummaryComponentDependency(4, 0, List.of(), false, 1, TIMESTAMP, TIMESTAMP, DURATION),
                new SummaryComponentDependency(5, 1, List.of(), false, 1, TIMESTAMP, TIMESTAMP, DURATION),
                new SummaryComponentDependency(6, 8, List.of(), false, 1, TIMESTAMP, TIMESTAMP, DURATION),
                new SummaryComponentDependency(7, 9, List.of(), false, 1, TIMESTAMP, TIMESTAMP, DURATION),
                new SummaryComponentDependency(10, 6, List.of(), false, 1, TIMESTAMP, TIMESTAMP, DURATION),
                new SummaryComponentDependency(11, 7, List.of(), false, 1, TIMESTAMP, TIMESTAMP, DURATION));
    }

    @Test
    public void scanShouldTransformSummaryWithCallGraphs() {
        // Given
        wireMockServer = zipkinWireMockFactory.createWithRealResponses(PORT);
        createZipkinScanner(true);
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        underTest.refresh(componentMetadata, List.of());
        Summary summary = Summary.EMPTY;

        // When
        Summary returnValue = underTest.transformSummary(summary);

        // Then
        assertThat(returnValue.getCallGraphs()).containsExactly(
                createTestCallGraph("test-service-2", "span-1", "test-service-2c", "span-1c", 1, 0),
                createTestCallGraph("test-service-2", "span-1", "test-service-2b", "span-1b", 0, 1),
                createTestCallGraph("test-service-1", "span-1", "test-service-1c", "span-1c", 1, 0),
                createTestCallGraph("test-service-1", "span-1", "test-service-1b", "span-1b", 0, 1),
                createTestCallGraph("test-service-2", "span-2", "test-service-2c", "span-2c", 1, 0),
                createTestCallGraph("test-service-2", "span-2", "test-service-2b", "span-2b", 0, 1),
                createTestCallGraph("test-service-1", "span-2", "test-service-1c", "span-2c", 1, 0),
                createTestCallGraph("test-service-1", "span-2", "test-service-1b", "span-2b", 0, 1));
    }
    
    private void createZipkinScanner(boolean zipkinEnabled) {
        String zipkinBaseUrl = Optional.ofNullable(wireMockServer).map(WireMockServer::baseUrl).orElse("http://localhost:" + PORT);
        ZipkinConfig config = new ZipkinConfig(zipkinEnabled, zipkinBaseUrl, Duration.ofMinutes(2), null, null, 100);
        GenericDependencyCollator genericDependencyCollator = new GenericDependencyCollator();
        ZipkinConfiguration configuration = new ZipkinConfiguration();
        DependencyDurationCalculator dependencyDurationCalculator = new DependencyDurationCalculator();
        DependencyHelper dependencyHelper = new DependencyHelper(dependencyDurationCalculator, new SubComponentDependencyTagFilter());
        Comparator<SummarySubComponentDependencyNode> subComponentNodeComparator = configuration.subComponentNodeComparator(new MapComparator<>());
        zipkinService = createZipkinService(config);
        underTest = new ZipkinScanner(
                config,
                zipkinService,
                new ComponentDependencyCollator(genericDependencyCollator, configuration.componentNodeComparator(), dependencyHelper),
                new SubComponentDependencyCollator(genericDependencyCollator, subComponentNodeComparator, dependencyHelper),
                new CallGraphCollator(genericDependencyCollator, subComponentNodeComparator, dependencyHelper, dependencyDurationCalculator)
        );
    }

    private ZipkinService createZipkinService(ZipkinConfig config) {
        return config.getEnabled() ?
                new ZipkinService(new ZipkinClient(config, WebClient.create(), Clock.systemUTC(), retryRegistry), config) :
                mock(ZipkinService.class);
    }

    private SummaryCallGraph createTestCallGraph(String componentId1, String spanName1, String componentId2, String spanName2, int sourceIndex, int targetIndex) {
        return SummaryCallGraph.builder()
                .nodes(List.of(
                        SummarySubComponentDependencyNode.builder()
                                .componentId(componentId1)
                                .spanName(componentId1 + "-" + spanName1)
                                .tags(Map.ofEntries(Map.entry("http.path_template", "/" + componentId1 + "-" + spanName1)))
                                .build(),
                        SummarySubComponentDependencyNode.builder()
                                .componentId(componentId2)
                                .spanName(componentId2 + "-" + spanName2)
                                .tags(Map.ofEntries(Map.entry("http.path_template", "/" + componentId2 + "-" + spanName2)))
                                .build()))
                .dependencies(List.of(
                        SummaryComponentDependency.builder()
                                .sourceIndex(sourceIndex)
                                .targetIndex(targetIndex)
                                .manual(false)
                                .sampleSize(1)
                                .startTimestamp(TIMESTAMP)
                                .endTimestamp(TIMESTAMP)
                                .duration(DURATION)
                                .build()))
                .traceCount(1)
                .build();
    }
}