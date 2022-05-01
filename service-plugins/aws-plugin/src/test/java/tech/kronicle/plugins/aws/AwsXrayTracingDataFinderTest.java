package tech.kronicle.plugins.aws;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.xray.services.DependencyService;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.Dependency;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
        List<Dependency> dependencies = createDependencies();
        when(dependencyService.getDependencies()).thenReturn(dependencies);

        // When
        Output<TracingData, Void> returnValue = underTest.find(ComponentMetadata.builder().build());

        // Then
        assertThat(returnValue).isEqualTo(Output.ofOutput(
                TracingData.builder()
                        .dependencies(dependencies)
                        .build(),
                CACHE_TTL
        ));
    }

    @Test
    public void findShouldReturnNoDependenciesWhenLoadXrayTraceDataIsFalse() {
        // Given
        AwsXrayTracingDataFinder underTest = createUnderTest(false);

        // When
        Output<TracingData, Void> returnValue = underTest.find(ComponentMetadata.builder().build());

        // Then
        assertThat(returnValue).isEqualTo(Output.ofOutput(TracingData.builder().build(), CACHE_TTL));
        verifyNoInteractions(dependencyService);
    }

    private List<Dependency> createDependencies() {
        return List.of(
                Dependency.builder()
                        .sourceComponentId("test-source-component-id-1")
                        .targetComponentId("test-target-component-id-2")
                        .build(),
                Dependency.builder()
                        .sourceComponentId("test-source-component-id-3")
                        .targetComponentId("test-target-component-id-4")
                        .build());
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
                        null
                )
        );
    }
}
