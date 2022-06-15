package tech.kronicle.plugins.aws;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.xray.services.DependencyService;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.Dependency;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static tech.kronicle.plugins.aws.testutils.AwsProfileAndRegionUtils.createProfileAndRegion;
import static tech.kronicle.plugins.aws.testutils.DependencyUtils.createDependency;

@ExtendWith(MockitoExtension.class)
public class AwsXrayTracingDataFinderTest {

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    @Mock
    public DependencyService dependencyService;

    @Test
    public void idShouldReturnTheIdOfTheFinder() {
        // Given
        AwsXrayTracingDataFinder underTest = createUnderTest(false);

        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("aws-xray-tracing-data");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheFinder() {
        // Given
        AwsXrayTracingDataFinder underTest = createUnderTest(false);

        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Fetches component dependencies from AWS X-Ray.  ");
    }

    @Test
    public void findShouldReturnAllDependenciesWhenLoadXrayTraceDataIsTrue() {
        // Given
        AwsXrayTracingDataFinder underTest = createUnderTest(true);
        List<Map.Entry<AwsProfileAndRegion, List<Dependency>>> dependenciesByProfileAndRegion = createDependenciesByProfileAndRegion();
        when(dependencyService.getDependencies()).thenReturn(dependenciesByProfileAndRegion);

        // When
        Output<List<TracingData>, Void> returnValue = underTest.find(ComponentMetadata.builder().build());

        // Then
        assertThat(returnValue).isEqualTo(Output.ofOutput(
                List.of(
                        TracingData.builder()
                                .id("aws-xray-service-graph-test-environment-id-1")
                                .name("AWS X-Ray Service Graph - test-environment-id-1")
                                .pluginId("aws")
                                .environmentId("test-environment-id-1")
                                .dependencies(dependenciesByProfileAndRegion.get(0).getValue())
                                .build(),
                        TracingData.builder()
                                .id("aws-xray-service-graph-test-environment-id-2")
                                .name("AWS X-Ray Service Graph - test-environment-id-2")
                                .pluginId("aws")
                                .environmentId("test-environment-id-2")
                                .dependencies(dependenciesByProfileAndRegion.get(1).getValue())
                                .build()
                ),
                CACHE_TTL
        ));
    }

    @Test
    public void findShouldReturnNoDependenciesWhenLoadXrayTraceDataIsFalse() {
        // Given
        AwsXrayTracingDataFinder underTest = createUnderTest(false);

        // When
        Output<List<TracingData>, Void> returnValue = underTest.find(ComponentMetadata.builder().build());

        // Then
        assertThat(returnValue).isEqualTo(Output.ofOutput(List.of(), CACHE_TTL));
        verifyNoInteractions(dependencyService);
    }

    private List<Map.Entry<AwsProfileAndRegion, List<Dependency>>> createDependenciesByProfileAndRegion() {
        return List.of(
                Map.entry(createProfileAndRegion(1), List.of(
                        createDependency(1),
                        createDependency(2)
                )),
                Map.entry(createProfileAndRegion(2), List.of(
                        createDependency(3),
                        createDependency(4)
                ))
        );
    }

    private AwsXrayTracingDataFinder createUnderTest(boolean loadXrayTraceData) {
        return new AwsXrayTracingDataFinder(
                dependencyService,
                new AwsConfig(
                        null,
                        null,
                        null,
                        null,
                        loadXrayTraceData,
                        null,
                        null,
                        null
                )
        );
    }
}
