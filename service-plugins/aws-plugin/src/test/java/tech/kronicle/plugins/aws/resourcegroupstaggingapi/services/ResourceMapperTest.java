package tech.kronicle.plugins.aws.resourcegroupstaggingapi.services;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.With;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.config.AwsProfileConfig;
import tech.kronicle.plugins.aws.config.AwsTagKeysConfig;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiTag;
import tech.kronicle.sdk.constants.ConnectionTypes;
import tech.kronicle.sdk.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.plugins.aws.testutils.ResourceGroupsTaggingApiResourceUtils.*;

public class ResourceMapperTest {

    private static final String TEST_ENVIRONMENT_ID = "test-environment-id";

    @Test
    public void mapResourcesShouldReturnAnEmptyListWhenResourceListIsEmpty() {
        // Given
        ResourceMapper underTest = createUnderTest();

        // When
        List<Component> returnValue = underTest.mapResourcesToComponents(TEST_ENVIRONMENT_ID, List.of());

        // Then
        assertThat(returnValue).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("provideMappingConfig")
    public void mapResourcesShouldMapAllResourcesToComponentsAndConnections(MappingConfig mappingConfig) {
        // Given
        ResourceMapper underTest = createUnderTest(mappingConfig);
        List<ResourceGroupsTaggingApiResource> resources = List.of(
                new ResourceGroupsTaggingApiResource(
                        "arn:aws:lambda:us-west-1:123456789012:function:ExampleStack-exampleFunction123ABC-123456ABCDEF",
                        List.of()
                ),
                new ResourceGroupsTaggingApiResource(
                        "arn:aws:ec2:us-west-1:123456789012:security-group/sg-12345678901ABCDEF",
                        prepareResourceTags(
                                mappingConfig,
                                List.of(
                                        new ResourceGroupsTaggingApiTag("Name", "Test name"),
                                        new ResourceGroupsTaggingApiTag("test-team-tag-key", "test-team-id"),
                                        new ResourceGroupsTaggingApiTag("test-component-tag-key", "test-component-id"),
                                        new ResourceGroupsTaggingApiTag("test-aliases-tag-key", "test-alias-id-1, test-alias-id-2"),
                                        new ResourceGroupsTaggingApiTag("test-tag-key-1", "test-tag-value-1")
                                ),
                                new ResourceGroupsTaggingApiTag("test-description-tag-key", "Test description"),
                                new ResourceGroupsTaggingApiTag("test-environment-tag-key", "test-override-environment-id")
                        )
                )
        );

        // When
        List<Component> returnValue = underTest.mapResourcesToComponents(TEST_ENVIRONMENT_ID, resources);

        // Then
        assertThat(returnValue).isEqualTo(List.of(
                Component.builder()
                        .id("aws.test-environment-id.lambda-function.examplestack-examplefunction123abc-123456abcdef")
                        .aliases(List.of(
                                Alias.builder().id("ExampleStack-exampleFunction123ABC-123456ABCDEF").build(),
                                Alias.builder().id("examplestack-examplefunction123abc-123456abcdef").build()
                        ))
                        .name("AWS - test-environment-id - ExampleStack-exampleFunction123ABC-123456ABCDEF")
                        .typeId("aws.lambda-function")
                        .description(prepareExpectedDescription(
                                mappingConfig,
                                null,
                                "arn:aws:lambda:us-west-1:123456789012:function:ExampleStack-exampleFunction123ABC-123456ABCDEF\n"
                        ))
                        .platformId("aws")
                        .states(List.of(
                                DiscoveredState.builder()
                                        .environmentId(TEST_ENVIRONMENT_ID)
                                        .pluginId("aws")
                                        .build()
                        ))
                        .build(),
                Component.builder()
                        .id("aws.test-environment-id.ec2-security-group.security-group-sg-12345678901abcdef")
                        .aliases(List.of(
                                Alias.builder().id("security-group/sg-12345678901ABCDEF").build(),
                                Alias.builder().id("security-group/sg-12345678901abcdef").build(),
                                Alias.builder().id("Test name").build(),
                                Alias.builder().id("test-alias-id-1").build(),
                                Alias.builder().id("test-alias-id-2").build()
                        ))
                        .name("AWS - test-environment-id - Test name")
                        .typeId("aws.ec2-security-group")
                        .tags(prepareExpectedTags(
                                mappingConfig,
                                List.of(
                                        Tag.builder().key("name").value("Test name").build(),
                                        Tag.builder().key("test-team-tag-key").value("test-team-id").build(),
                                        Tag.builder().key("test-component-tag-key").value("test-component-id").build(),
                                        Tag.builder().key("test-aliases-tag-key").value("test-alias-id-1, test-alias-id-2").build(),
                                        Tag.builder().key("test-tag-key-1").value("test-tag-value-1").build()
                                ),
                                Tag.builder().key("test-description-tag-key").value("Test description").build(),
                                Tag.builder().key("test-environment-tag-key").value("test-override-environment-id").build()
                        ))
                        .teams(List.of(
                                ComponentTeam.builder()
                                        .teamId("test-team-id")
                                        .build()
                        ))
                        .description(prepareExpectedDescription(
                                mappingConfig,
                                "Test description",
                                "arn:aws:ec2:us-west-1:123456789012:security-group/sg-12345678901ABCDEF\n"
                                        + "\n"
                                        + "Tags:\n"
                                        + "\n"
                                        + "* Name=Test name\n"
                                        + "* test-team-tag-key=test-team-id\n"
                                        + "* test-component-tag-key=test-component-id\n"
                                        + "* test-aliases-tag-key=test-alias-id-1, test-alias-id-2\n"
                                        + "* test-tag-key-1=test-tag-value-1\n"
                                        + (mappingConfig.environmentTag ? "* test-environment-tag-key=test-override-environment-id\n" : "")
                                        + "\n"
                                        + "Aliases:\n"
                                        + "\n"
                                        + "* security-group/sg-12345678901ABCDEF\n"
                                        + "* security-group/sg-12345678901abcdef\n"
                                        + "* Test name\n"
                                        + "* test-alias-id-1\n"
                                        + "* test-alias-id-2\n"
                        ))
                        .platformId("aws")
                        .connections(prepareExpectedConnections(mappingConfig))
                        .states(List.of(
                                DiscoveredState.builder()
                                        .environmentId(prepareExpectedEnvironmentId(mappingConfig, TEST_ENVIRONMENT_ID))
                                        .pluginId("aws")
                                        .build()
                        ))
                        .build()
                )
        );
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
                                        null,
                                        TEST_ENVIRONMENT_ID
                                )
                        ),
                        mappingConfig.detailedComponentDescriptions,
                        mappingConfig.copyResourceTagsToComponents,
                        mappingConfig.createDependenciesForResources,
                        null,
                        new AwsTagKeysConfig(
                                TEST_ALIASES_TAG_KEY,
                                TEST_COMPONENT_TAG_KEY,
                                TEST_DESCRIPTION_TAG_KEY,
                                TEST_ENVIRONMENT_TAG_KEY,
                                TEST_TEAM_TAG_KEY
                        ),
                        null,
                        null
                )
        );
    }

    public static Stream<MappingConfig> provideMappingConfig() {
        return Stream.of(
                MappingConfig.builder().build(),
                MappingConfig.builder().descriptionTag().build(),
                MappingConfig.builder().environmentTag().build(),
                MappingConfig.builder().detailedComponentDescriptions().build(),
                MappingConfig.builder().copyResourceTagsToComponents().build(),
                MappingConfig.builder().createDependenciesForResources().build(),
                MappingConfig.builder()
                        .descriptionTag()
                        .environmentTag()
                        .detailedComponentDescriptions()
                        .copyResourceTagsToComponents()
                        .createDependenciesForResources()
                        .build()
        );
    }

    private List<ResourceGroupsTaggingApiTag> prepareResourceTags(
            MappingConfig mappingConfig,
            List<ResourceGroupsTaggingApiTag> tags,
            ResourceGroupsTaggingApiTag descriptionTag,
            ResourceGroupsTaggingApiTag environmentTag
    ) {
        List<ResourceGroupsTaggingApiTag> expectedTags = new ArrayList<>(tags);
        if (mappingConfig.descriptionTag) {
            expectedTags.add(descriptionTag);
        }
        if (mappingConfig.environmentTag) {
            expectedTags.add(environmentTag);
        }
        return expectedTags;
    }

    private String prepareExpectedDescription(MappingConfig mappingConfig, String descriptionTagValue, String detailedDescription) {
        if (mappingConfig.descriptionTag && nonNull(descriptionTagValue)) {
            return descriptionTagValue;
        } else if (mappingConfig.detailedComponentDescriptions) {
            return detailedDescription;
        } else {
            return "";
        }
    }

    private List<Tag> prepareExpectedTags(
            MappingConfig mappingConfig,
            List<Tag> tags,
            Tag descriptionTag,
            Tag environmentTag
    ) {
        if (mappingConfig.copyResourceTagsToComponents) {
            List<Tag> expectedTags = new ArrayList<>(tags);
            if (mappingConfig.descriptionTag) {
                expectedTags.add(descriptionTag);
            }
            if (mappingConfig.environmentTag) {
                expectedTags.add(environmentTag);
            }
            return expectedTags;
        } else {
            return List.of();
        }
    }

    private List<ComponentConnection> prepareExpectedConnections(MappingConfig mappingConfig) {
        if (mappingConfig.createDependenciesForResources) {
            return List.of(
                    ComponentConnection.builder()
                            .targetComponentId("test-component-id")
                            .type(ConnectionTypes.SUPER_COMPONENT)
                            .label("is composed of")
                            .build()
            );
        } else {
            return List.of();
        }
    }

    private String prepareExpectedEnvironmentId(MappingConfig mappingConfig, String environmentId) {
        return mappingConfig.environmentTag ? "test-override-environment-id" : environmentId;
    }

    @With
    @ToString
    @RequiredArgsConstructor
    private static class MappingConfig {

        private final boolean descriptionTag;
        private final boolean environmentTag;
        private final boolean detailedComponentDescriptions;
        private final boolean copyResourceTagsToComponents;
        private final boolean createDependenciesForResources;

        private static MappingConfigBuilder builder() {
            return new MappingConfigBuilder();
        }

        private static class MappingConfigBuilder {

            private boolean descriptionTag;
            private boolean environmentTag;
            private boolean detailedComponentDescriptions;
            private boolean copyResourceTagsToComponents;
            private boolean createDependenciesForResources;

            public MappingConfigBuilder descriptionTag() {
                descriptionTag = true;
                return this;
            }

            public MappingConfigBuilder environmentTag() {
                environmentTag = true;
                return this;
            }
            
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
                        descriptionTag,
                        environmentTag,
                        detailedComponentDescriptions,
                        copyResourceTagsToComponents,
                        createDependenciesForResources
                );
            }
        }
    }
}
