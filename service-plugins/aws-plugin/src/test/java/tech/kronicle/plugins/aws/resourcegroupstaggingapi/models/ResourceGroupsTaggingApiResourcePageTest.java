package tech.kronicle.plugins.aws.resourcegroupstaggingapi.models;

import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResourcePage;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiTag;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ResourceGroupsTaggingApiResourcePageTest {

    @Test
    public void constructorShouldMakeTagsAnUnmodifiableList() {
        // Given
        ResourceGroupsTaggingApiResourcePage underTest = new ResourceGroupsTaggingApiResourcePage(new ArrayList<>(), null);

        // When
        Throwable thrown = catchThrowable(() -> underTest.getItems().add(
                new ResourceGroupsTaggingApiResource(null, null)
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
