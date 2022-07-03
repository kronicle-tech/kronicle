package tech.kronicle.plugins.aws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.models.ComponentsAndDiagrams;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.services.ResourceService;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.Diagram;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AwsComponentFinderTest {

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    public AwsComponentFinder underTest;
    @Mock
    public ResourceService resourceService;

    @BeforeEach
    public void beforeEach() {
        underTest = new AwsComponentFinder(resourceService);
    }

    @Test
    public void idShouldReturnTheIdOfTheFinder() {
        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("aws-component");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheFinder() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Fetches components from AWS.  ");
    }

    @Test
    public void findShouldReturnAllComponentsAndDiagrams() {
        // Given
        ComponentsAndDiagrams componentsAndDiagrams = new ComponentsAndDiagrams(
                List.of(
                        Component.builder()
                                .id("test-component-id-1")
                                .build(),
                        Component.builder()
                                .id("test-component-id-2")
                                .build()
                ),
                List.of(
                        Diagram.builder()
                                .id("test-diagram-id-1")
                                .build(),
                        Diagram.builder()
                                .id("test-diagram-id-2")
                                .build()
                )
        );
        when(resourceService.getComponentsAndDiagrams()).thenReturn(componentsAndDiagrams);

        // When
        Output<ComponentsAndDiagrams, Void> returnValue = underTest.find(ComponentMetadata.builder().build());

        // Then
        assertThat(returnValue).isEqualTo(Output.ofOutput(componentsAndDiagrams, CACHE_TTL));
    }
}
