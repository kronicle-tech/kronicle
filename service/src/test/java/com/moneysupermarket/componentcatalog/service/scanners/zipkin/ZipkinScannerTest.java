package com.moneysupermarket.componentcatalog.service.scanners.zipkin;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.moneysupermarket.componentcatalog.componentmetadata.models.ComponentMetadata;
import com.moneysupermarket.componentcatalog.sdk.models.Component;
import com.moneysupermarket.componentcatalog.sdk.models.Summary;
import com.moneysupermarket.componentcatalog.sdk.models.SummaryCallGraph;
import com.moneysupermarket.componentcatalog.sdk.models.SummaryComponentDependency;
import com.moneysupermarket.componentcatalog.sdk.models.SummaryComponentDependencyDuration;
import com.moneysupermarket.componentcatalog.sdk.models.SummaryComponentDependencyNode;
import com.moneysupermarket.componentcatalog.sdk.models.SummarySubComponentDependencyNode;
import com.moneysupermarket.componentcatalog.sdk.models.zipkin.Zipkin;
import com.moneysupermarket.componentcatalog.sdk.models.zipkin.ZipkinDependency;
import com.moneysupermarket.componentcatalog.service.scanners.BaseScannerTest;
import com.moneysupermarket.componentcatalog.service.scanners.models.Output;
import com.moneysupermarket.componentcatalog.service.scanners.zipkin.client.ZipkinClient;
import com.moneysupermarket.componentcatalog.service.scanners.zipkin.config.ZipkinConfig;
import com.moneysupermarket.componentcatalog.service.scanners.zipkin.services.CallGraphCollator;
import com.moneysupermarket.componentcatalog.service.scanners.zipkin.services.ComponentDependencyCollator;
import com.moneysupermarket.componentcatalog.service.scanners.zipkin.services.DependencyDurationCalculator;
import com.moneysupermarket.componentcatalog.service.scanners.zipkin.services.DependencyHelper;
import com.moneysupermarket.componentcatalog.service.scanners.zipkin.services.GenericDependencyCollator;
import com.moneysupermarket.componentcatalog.service.scanners.zipkin.services.SubComponentDependencyCollator;
import com.moneysupermarket.componentcatalog.service.scanners.zipkin.services.SubComponentDependencyTagFilter;
import com.moneysupermarket.componentcatalog.service.scanners.zipkin.services.ZipkinService;
import com.moneysupermarket.componentcatalog.service.scanners.zipkin.spring.ZipkinConfiguration;
import com.moneysupermarket.componentcatalog.service.services.MapComparator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ZipkinScannerTest extends BaseScannerTest {

    private static final int PORT = 36206;
    private static final LocalDateTime TIMESTAMP = LocalDateTime.of(2021, 1, 1, 0, 0);
    private static final SummaryComponentDependencyDuration DURATION = new SummaryComponentDependencyDuration(123L, 123L, 123L, 123L, 123L, 123L);

    private ZipkinScanner underTest;
    private WireMockServer wireMockServer;

    @BeforeEach
    public void beforeEach() {
        wireMockServer = ZipkinWireMockFactory.createWithRealResponses(PORT);
        createZipkinScanner();
    }

    @AfterEach
    public void afterEach() {
        wireMockServer.stop();
    }

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("zipkin");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Scans a Zipkin Server instance using its Zipkin API endpoints");
    }

    @Test
    public void notesShouldReturnTheNotesForTheScanner() {
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
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();

        // When
        // No exception should be raised
        underTest.refresh(componentMetadata);
    }

    @Test
    public void scanShouldReturnUpstreamAndDownstreamDependenciesOfAService() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        underTest.refresh(componentMetadata);
        Component component = Component.builder()
                .id("test-service-1")
                .build();

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
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        underTest.refresh(componentMetadata);
        Component component = Component.builder()
                .id("unknown-service")
                .build();

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
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        underTest.refresh(componentMetadata);
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
    public void scanShouldTransformSummaryWithSubComponentDependencies() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        underTest.refresh(componentMetadata);
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
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        underTest.refresh(componentMetadata);
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
    
    private void createZipkinScanner() {
        ZipkinConfig config = new ZipkinConfig(wireMockServer.baseUrl(), Duration.ofMinutes(2), null, null, 100, null);
        GenericDependencyCollator genericDependencyCollator = new GenericDependencyCollator();
        ZipkinConfiguration configuration = new ZipkinConfiguration();
        DependencyDurationCalculator dependencyDurationCalculator = new DependencyDurationCalculator();
        DependencyHelper dependencyHelper = new DependencyHelper(dependencyDurationCalculator, new SubComponentDependencyTagFilter());
        Comparator<SummarySubComponentDependencyNode> subComponentNodeComparator = configuration.subComponentNodeComparator(new MapComparator<>());
        underTest = new ZipkinScanner(
                new ZipkinService(new ZipkinClient(WebClient.create(), config, Clock.systemUTC()), config),
                new ComponentDependencyCollator(genericDependencyCollator, configuration.componentNodeComparator(), dependencyHelper),
                new SubComponentDependencyCollator(genericDependencyCollator, subComponentNodeComparator, dependencyHelper),
                new CallGraphCollator(genericDependencyCollator, subComponentNodeComparator, dependencyHelper, dependencyDurationCalculator));
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
