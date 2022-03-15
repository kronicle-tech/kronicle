package tech.kronicle.plugins.aws.resourcegroupstaggingapi.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiTag;
import tech.kronicle.sdk.models.Alias;
import tech.kronicle.sdk.models.Component;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceMapperTest {

    @Test
    public void mapResourcesShouldReturnAnEmptyListWhenResourceListIsEmpty() {
        // Given
        ResourceMapper underTest = new ResourceMapper();

        // When
        List<Component> components = underTest.mapResources(List.of());

        // Then
        assertThat(components).isEmpty();
    }

    @Test
    public void mapResourcesShouldMapAllResourcesToComponents() {
        // Given
        ResourceMapper underTest = new ResourceMapper();
        List<ResourceGroupsTaggingApiResource> resources = List.of(
                new ResourceGroupsTaggingApiResource(
                        "arn:aws:lambda:us-west-1:123456789012:function:ExampleStack-exampleFunction123ABC-123456ABCDEF",
                        List.of()
                ),
                new ResourceGroupsTaggingApiResource(
                        "arn:aws:ec2:us-west-1:123456789012:security-group/sg-12345678901ABCDEF",
                        List.of(
                                new ResourceGroupsTaggingApiTag("Name", "Test name"),
                                new ResourceGroupsTaggingApiTag("test-tag-key-1", "test-tag-value-1")
                        )
                )
        );

        // When
        List<Component> returnValue = underTest.mapResources(resources);

        // Then
        assertThat(returnValue).isEqualTo(List.of(
                Component.builder()
                        .id("aws-lambda-function-examplestack-examplefunction123abc-123456abcdef")
                        .aliases(List.of(
                                Alias.builder().id("ExampleStack-exampleFunction123ABC-123456ABCDEF").build(),
                                Alias.builder().id("examplestack-examplefunction123abc-123456abcdef").build()
                        ))
                        .name("ExampleStack-exampleFunction123ABC-123456ABCDEF")
                        .typeId("aws-lambda-function")
                        .description("arn:aws:lambda:us-west-1:123456789012:function:ExampleStack-exampleFunction123ABC-123456ABCDEF\n")
                        .build(),
                Component.builder()
                        .id("aws-ec2-security-group-security-group-sg-12345678901abcdef")
                        .aliases(List.of(
                                Alias.builder().id("security-group/sg-12345678901ABCDEF").build(),
                                Alias.builder().id("security-group/sg-12345678901abcdef").build()
                        ))
                        .name("Test name")
                        .typeId("aws-ec2-security-group")
                        .description("arn:aws:ec2:us-west-1:123456789012:security-group/sg-12345678901ABCDEF\n" +
                                "\n" +
                                "Tags:\n" +
                                "\n" +
                                "* Name=Test name\n" +
                                "* test-tag-key-1=test-tag-value-1\n" +
                                "\n" +
                                "Aliases:\n" +
                                "\n" +
                                "* Alias(id=security-group/sg-12345678901ABCDEF, description=null, notes=null)\n" +
                                "* Alias(id=security-group/sg-12345678901abcdef, description=null, notes=null)\n")
                        .build()
        ));
    }
}
