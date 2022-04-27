package tech.kronicle.plugins.aws;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.xray.services.DependencyService;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.Dependency;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AwsXrayTracingDataFinderTest {

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
        TracingData returnValue = underTest.find(ComponentMetadata.builder().build());

        // Then
        assertThat(returnValue).isEqualTo(TracingData.builder()
                .dependencies(dependencies)
                .build());
    }

    @Test
    public void findShouldReturnNoDependenciesWhenLoadXrayTraceDataIsFalse() {
        // Given
        AwsXrayTracingDataFinder underTest = createUnderTest(false);
        List<Dependency> dependencies = createDependencies();

        // When
        TracingData returnValue = underTest.find(ComponentMetadata.builder().build());

        // Then
        assertThat(returnValue).isEqualTo(TracingData.builder()
                .build());
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
