package tech.kronicle.plugins.aws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.plugins.aws.xray.services.DependencyService;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.Dependency;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AwsXrayTracingDataFinderTest {

    public AwsXrayTracingDataFinder underTest;
    @Mock
    public DependencyService dependencyService;

    @BeforeEach
    public void beforeEach() {
        underTest = new AwsXrayTracingDataFinder(dependencyService);
    }

    @Test
    public void idShouldReturnTheIdOfTheFinder() {
        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("aws-xray-tracing-data");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheFinder() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Fetches component dependencies from AWS X-Ray.  ");
    }

    @Test
    public void findShouldReturnAllDependencies() {
        // Given
        List<Dependency> dependencies = List.of(
                Dependency.builder()
                        .sourceComponentId("test-source-component-id-1")
                        .targetComponentId("test-target-component-id-2")
                        .build(),
                Dependency.builder()
                        .sourceComponentId("test-source-component-id-3")
                        .targetComponentId("test-target-component-id-4")
                        .build());
        when(dependencyService.getDependencies()).thenReturn(dependencies);

        // When
        TracingData returnValue = underTest.find(ComponentMetadata.builder().build());

        // Then
        assertThat(returnValue).isEqualTo(TracingData.builder()
                .dependencies(dependencies)
                .build());
    }
}
