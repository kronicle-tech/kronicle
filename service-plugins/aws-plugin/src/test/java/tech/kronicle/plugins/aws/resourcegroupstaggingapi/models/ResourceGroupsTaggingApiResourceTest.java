package tech.kronicle.plugins.aws.resourcegroupstaggingapi.models;

import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiTag;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ResourceGroupsTaggingApiResourceTest {

    @Test
    public void constructorShouldMakeTagsAnUnmodifiableList() {
        // Given
        ResourceGroupsTaggingApiResource underTest = new ResourceGroupsTaggingApiResource(null, new ArrayList<>());

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTags().add(
                new ResourceGroupsTaggingApiTag(null, null)
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
