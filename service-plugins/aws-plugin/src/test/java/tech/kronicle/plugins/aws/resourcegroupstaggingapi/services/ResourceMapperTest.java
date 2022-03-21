package tech.kronicle.plugins.aws.resourcegroupstaggingapi.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiTag;
import tech.kronicle.sdk.models.Alias;
import tech.kronicle.sdk.models.Component;

import java.util.List;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class ResourceMapperTest {

    @Test
    public void mapResourcesShouldReturnAnEmptyListWhenResourceListIsEmpty() {
        // Given
        ResourceMapper underTest = createUnderTest(false);

        // When
        List<Component> components = underTest.mapResources(List.of());

        // Then
        assertThat(components).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    @NullSource
    public void mapResourcesShouldMapAllResourcesToComponents(Boolean detailedComponentDescriptions) {
        // Given
        ResourceMapper underTest = createUnderTest(detailedComponentDescriptions);
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
                        .description(prepareExpectedDescription(
                                detailedComponentDescriptions,
                                "arn:aws:lambda:us-west-1:123456789012:function:ExampleStack-exampleFunction123ABC-123456ABCDEF\n"
                        ))
                        .platformId("aws-managed-service")
                        .build(),
                Component.builder()
                        .id("aws-ec2-security-group-security-group-sg-12345678901abcdef")
                        .aliases(List.of(
                                Alias.builder().id("security-group/sg-12345678901ABCDEF").build(),
                                Alias.builder().id("security-group/sg-12345678901abcdef").build()
                        ))
                        .name("Test name")
                        .typeId("aws-ec2-security-group")
                        .description(prepareExpectedDescription(
                                detailedComponentDescriptions,
                                        "arn:aws:ec2:us-west-1:123456789012:security-group/sg-12345678901ABCDEF\n" +
                                        "\n" +
                                        "Tags:\n" +
                                        "\n" +
                                        "* Name=Test name\n" +
                                        "* test-tag-key-1=test-tag-value-1\n" +
                                        "\n" +
                                        "Aliases:\n" +
                                        "\n" +
                                        "* Alias(id=security-group/sg-12345678901ABCDEF, description=null, notes=null)\n" +
                                        "* Alias(id=security-group/sg-12345678901abcdef, description=null, notes=null)\n"
                        ))
                        .platformId("aws-managed-service")
                        .build()
        ));
    }

    private ResourceMapper createUnderTest(Boolean detailedComponentDescriptions) {
        return new ResourceMapper(new AwsConfig(null, detailedComponentDescriptions));
    }

    private String prepareExpectedDescription(Boolean detailedComponentDescriptions, String value) {
        return nonNull(detailedComponentDescriptions) && detailedComponentDescriptions
                ? value
                : "";
    }
}
