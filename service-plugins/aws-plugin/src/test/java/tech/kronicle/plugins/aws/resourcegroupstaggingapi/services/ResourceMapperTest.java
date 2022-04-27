package tech.kronicle.plugins.aws.resourcegroupstaggingapi.services;

import lombok.RequiredArgsConstructor;
import lombok.With;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.config.AwsProfileConfig;
import tech.kronicle.plugins.aws.config.AwsTagKeysConfig;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiTag;
import tech.kronicle.sdk.constants.DependencyTypeIds;
import tech.kronicle.sdk.models.Alias;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentDependency;
import tech.kronicle.sdk.models.ComponentState;
import tech.kronicle.sdk.models.ComponentTeam;
import tech.kronicle.sdk.models.DependencyDirection;
import tech.kronicle.sdk.models.EnvironmentState;
import tech.kronicle.sdk.models.Tag;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceMapperTest {

    private static final String TEST_ENVIRONMENT_ID = "test-environment-id";

    @Test
    public void mapResourcesShouldReturnAnEmptyListWhenResourceListIsEmpty() {
        // Given
        ResourceMapper underTest = createUnderTest();

        // When
        List<Component> components = underTest.mapResourcesToComponents(TEST_ENVIRONMENT_ID, List.of());

        // Then
        assertThat(components).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("provideMappingConfig")
    public void mapResourcesShouldMapAllResourcesToComponents(MappingConfig mappingConfig) {
        // Given
        ResourceMapper underTest = createUnderTest(mappingConfig);
        List<ResourceGroupsTaggingApiResource> resources = List.of(
                new ResourceGroupsTaggingApiResource(
                        "arn:aws:lambda:us-west-1:123456789012:function:ExampleStack-exampleFunction123ABC-123456ABCDEF",
                        List.of()
                ),
                new ResourceGroupsTaggingApiResource(
                        "arn:aws:ec2:us-west-1:123456789012:security-group/sg-12345678901ABCDEF",
                        List.of(
                                new ResourceGroupsTaggingApiTag("Name", "Test name"),
                                new ResourceGroupsTaggingApiTag("test-team-tag-key", "test-team-id"),
                                new ResourceGroupsTaggingApiTag("test-component-tag-key", "test-component-id"),
                                new ResourceGroupsTaggingApiTag("test-tag-key-1", "test-tag-value-1")
                        )
                )
        );

        // When
        List<Component> returnValue = underTest.mapResourcesToComponents(TEST_ENVIRONMENT_ID, resources);

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
                                mappingConfig,
                                "arn:aws:lambda:us-west-1:123456789012:function:ExampleStack-exampleFunction123ABC-123456ABCDEF\n"
                        ))
                        .platformId("aws-managed-service")
                        .state(ComponentState.builder()
                                .environments(List.of(
                                        EnvironmentState.builder()
                                                .id(TEST_ENVIRONMENT_ID)
                                                .build()
                                ))
                                .build()
                        )
                        .build(),
                Component.builder()
                        .id("aws-ec2-security-group-security-group-sg-12345678901abcdef")
                        .aliases(List.of(
                                Alias.builder().id("security-group/sg-12345678901ABCDEF").build(),
                                Alias.builder().id("security-group/sg-12345678901abcdef").build(),
                                Alias.builder().id("Test name").build()
                        ))
                        .name("Test name")
                        .typeId("aws-ec2-security-group")
                        .tags(prepareExpectedTags(
                                mappingConfig,
                                List.of(
                                        Tag.builder().key("name").value("Test name").build(),
                                        Tag.builder().key("test-team-tag-key").value("test-team-id").build(),
                                        Tag.builder().key("test-component-tag-key").value("test-component-id").build(),
                                        Tag.builder().key("test-tag-key-1").value("test-tag-value-1").build()
                                )
                        ))
                        .teams(List.of(
                                ComponentTeam.builder()
                                        .teamId("test-team-id")
                                        .build()
                        ))
                        .description(prepareExpectedDescription(
                                mappingConfig,
                                "arn:aws:ec2:us-west-1:123456789012:security-group/sg-12345678901ABCDEF\n" +
                                "\n" +
                                "Tags:\n" +
                                "\n" +
                                "* Name=Test name\n" +
                                "* test-team-tag-key=test-team-id\n" +
                                "* test-component-tag-key=test-component-id\n" +
                                "* test-tag-key-1=test-tag-value-1\n" +
                                "\n" +
                                "Aliases:\n" +
                                "\n" +
                                "* security-group/sg-12345678901ABCDEF\n" +
                                "* security-group/sg-12345678901abcdef\n" +
                                "* Test name\n"
                        ))
                        .platformId("aws-managed-service")
                        .dependencies(prepareExpectedDependencies(mappingConfig))
                        .state(ComponentState.builder()
                                .environments(List.of(
                                        EnvironmentState.builder()
                                                .id(TEST_ENVIRONMENT_ID)
                                                .build()
                                ))
                                .build()
                        )
                        .build()
        ));
    }

    private ResourceMapper createUnderTest() {
        return createUnderTest(MappingConfig.builder().build());
    }

    private ResourceMapper createUnderTest(MappingConfig mappingConfig) {
        return new ResourceMapper(
                new AwsConfig(
                        List.of(
                                new AwsProfileConfig(
                                        null,
                                        null,
                                        null,
                                        TEST_ENVIRONMENT_ID
                                )
                        ),
                        mappingConfig.detailedComponentDescriptions,
                        mappingConfig.copyResourceTagsToComponents,
                        mappingConfig.createDependenciesForResources,
                        null,
                        new AwsTagKeysConfig("test-component-tag-key", "test-team-tag-key"),
                        null
                )
        );
    }

    public static Stream<MappingConfig> provideMappingConfig() {
        return Stream.of(
                MappingConfig.builder().build(),
                MappingConfig.builder().detailedComponentDescriptions().build(),
                MappingConfig.builder().copyResourceTagsToComponents().build(),
                MappingConfig.builder().createDependenciesForResources().build(),
                MappingConfig.builder()
                        .detailedComponentDescriptions()
                        .copyResourceTagsToComponents()
                        .createDependenciesForResources()
                        .build()
        );
    }

    @RequiredArgsConstructor
    @With
    private static class MappingConfig {

        private final boolean detailedComponentDescriptions;
        private final boolean copyResourceTagsToComponents;
        private final boolean createDependenciesForResources;

        private static MappingConfigBuilder builder() {
            return new MappingConfigBuilder();
        }

        private static class MappingConfigBuilder {

            private boolean detailedComponentDescriptions;
            private boolean copyResourceTagsToComponents;
            private boolean createDependenciesForResources;

            public MappingConfigBuilder detailedComponentDescriptions() {
                detailedComponentDescriptions = true;
                return this;
            }

            public MappingConfigBuilder copyResourceTagsToComponents() {
                copyResourceTagsToComponents = true;
                return this;
            }

            public MappingConfigBuilder createDependenciesForResources() {
                createDependenciesForResources = true;
                return this;
            }

            public MappingConfig build() {
                return new MappingConfig(
                        detailedComponentDescriptions,
                        copyResourceTagsToComponents,
                        createDependenciesForResources
                );
            }
        }
    }

    private String prepareExpectedDescription(MappingConfig mappingConfig, String description) {
        return mappingConfig.detailedComponentDescriptions ? description : "";
    }

    private List<Tag> prepareExpectedTags(MappingConfig mappingConfig, List<Tag> tags) {
        return mappingConfig.copyResourceTagsToComponents ? tags : List.of();
    }

    private List<ComponentDependency> prepareExpectedDependencies(MappingConfig mappingConfig) {
        if (mappingConfig.createDependenciesForResources) {
            return List.of(
                    ComponentDependency.builder()
                            .targetComponentId("test-component-id")
                            .direction(DependencyDirection.INBOUND)
                            .typeId(DependencyTypeIds.COMPOSITION)
                            .label("is composed of")
                            .build()
            );
        } else {
            return List.of();
        }
    }
}
