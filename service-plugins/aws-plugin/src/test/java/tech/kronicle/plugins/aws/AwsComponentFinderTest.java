package tech.kronicle.plugins.aws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.services.ResourceService;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AwsComponentFinderTest {

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
    public void findShouldReturnAllDependencies() {
        // Given
        List<Component> components = List.of(
                Component.builder()
                        .id("test-component-id-1")
                        .build(),
                Component.builder()
                        .id("test-component-id-2")
                        .build()
        );
        when(resourceService.getComponents()).thenReturn(components);

        // When
        List<Component> returnValue = underTest.find(ComponentMetadata.builder().build());

        // Then
        assertThat(returnValue).isEqualTo(components);
    }
}
