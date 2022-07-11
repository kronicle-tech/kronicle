package tech.kronicle.plugins.aws.resourcegroupstaggingapi.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.aws.AwsPlugin;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.config.AwsProfileConfig;
import tech.kronicle.plugins.aws.constants.TagKeys;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiTag;
import tech.kronicle.plugins.aws.utils.AnalysedArn;
import tech.kronicle.sdk.constants.ConnectionTypes;
import tech.kronicle.sdk.models.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static tech.kronicle.common.CaseUtils.toKebabCase;
import static tech.kronicle.plugins.aws.resourcegroupstaggingapi.utils.ResourceUtils.getOptionalResourceTagValue;
import static tech.kronicle.plugins.aws.utils.ArnAnalyser.analyseArn;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ResourceMapper {

    private final AwsConfig config;

    public List<Component> mapResourcesToComponents(
            AwsProfileConfig profile,
            List<ResourceGroupsTaggingApiResource> resources
    ) {
        return resources.stream()
                .map(resource -> mapResourceToComponent(profile, resource))
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private Component mapResourceToComponent(AwsProfileConfig profile, ResourceGroupsTaggingApiResource resource) {
        if (resourcesWithSupportedMetadataOnly(profile) && !resourceHasSupportedMetadata(resource)) {
            return null;
        }
        AnalysedArn analysedArn = analyseArn(resource.getArn());
        Optional<String> nameTag = getNameTag(resource);
        String name = getName(nameTag, analysedArn);
        Optional<String> aliasesTag = getAliasesTag(resource);
        List<Alias> aliases = getAliases(analysedArn, name, aliasesTag);
        String updatedEnvironmentId1 = getEnvironmentId(resource, profile.getEnvironmentId());
        return Component.builder()
                .id("aws." + updatedEnvironmentId1 + "." + toKebabCase(analysedArn.getDerivedResourceType()) + "." + toKebabCase(analysedArn.getResourceId()))
                .aliases(aliases)
                .name("AWS - " + updatedEnvironmentId1 + " - " + name)
                .typeId(mapType(analysedArn))
                .tags(mapTags(resource))
                .teams(getTeam(resource))
                .description(getDescription(resource, analysedArn, aliases))
                .platformId("aws")
                .connections(mapResourceToConnections(updatedEnvironmentId1, resource))
                .states(List.of(
                        DiscoveredState.builder()
                                .environmentId(updatedEnvironmentId1)
                                .pluginId(AwsPlugin.ID)
                                .build()
                ))
                .build();
    }

    private boolean resourcesWithSupportedMetadataOnly(AwsProfileConfig profile) {
        Boolean value = profile.getApiResourcesWithSupportedMetadataOnly();
        return nonNull(value) ? value : false;
    }

    private boolean resourceHasSupportedMetadata(ResourceGroupsTaggingApiResource resource) {
        Set<String> tagKeys = resource.getTags().stream()
                .map(ResourceGroupsTaggingApiTag::getKey)
                .collect(toUnmodifiableSet());
        Set<String> supportedTagKeys = Set.of(
                config.getTagKeys().getAliases(),
                config.getTagKeys().getComponent(),
                config.getTagKeys().getDescription(),
                config.getTagKeys().getEnvironment(),
                config.getTagKeys().getTeam()
        );
        return supportedTagKeys.stream()
                .anyMatch(tagKeys::contains);
    }

    private String mapType(AnalysedArn analysedArn) {
        return "aws." + toKebabCase(analysedArn.getDerivedResourceType());
    }

    private List<ComponentConnection> mapResourceToConnections(
            String environmentId,
            ResourceGroupsTaggingApiResource resource
    ) {
        if (!config.getCreateDependenciesForResources()) {
            return List.of();
        }
        Optional<String> componentTag = getComponentTag(resource);
        if (componentTag.isEmpty()) {
            return List.of();
        }
        return List.of(ComponentConnection.builder()
                .targetComponentId(componentTag.get())
                .type(ConnectionTypes.SUPER_COMPONENT)
                .environmentId(environmentId)
                .label("is composed of")
                .build());
    }

    private List<Tag> mapTags(ResourceGroupsTaggingApiResource resource) {
        if (config.getCopyResourceTagsToComponents()) {
            return resource.getTags().stream()
                    .map(this::mapTag)
                    .collect(toUnmodifiableList());
        } else {
            return List.of();
        }
    }

    private Tag mapTag(ResourceGroupsTaggingApiTag resourceGroupsTaggingApiTag) {
        return Tag.builder()
                .key(toKebabCase(resourceGroupsTaggingApiTag.getKey()))
                .value(resourceGroupsTaggingApiTag.getValue())
                .build();
    }

    private List<Alias> getAliases(AnalysedArn analysedArn, String name, Optional<String> aliasesTag) {
        List<String> aliases = new ArrayList<>();
        aliases.add(analysedArn.getResourceId());
        aliases.add(analysedArn.getResourceId().toLowerCase());
        aliases.add(name);
        aliases.addAll(getAliasesFromTagValue(aliasesTag));
        return aliases.stream()
                .map(alias -> Alias.builder().id(alias).build())
                .distinct()
                .collect(toList());
    }

    private List<String> getAliasesFromTagValue(Optional<String> aliasesTag) {
        return aliasesTag.map(value -> value.split(","))
                .map(aliases -> Arrays.stream(aliases)
                        .map(String::trim)
                        .filter(alias -> !alias.isEmpty())
                        .collect(toUnmodifiableList())
                )
                .orElse(List.of());
    }

    private String getName(Optional<String> nameTag, AnalysedArn analysedArn) {
        return nameTag.orElseGet(analysedArn::getResourceId);
    }

    private Optional<String> getNameTag(ResourceGroupsTaggingApiResource resource) {
        return getOptionalResourceTagValue(resource, TagKeys.PASCAL_CASE_NAME);
    }

    private Optional<String> getAliasesTag(ResourceGroupsTaggingApiResource resource) {
        return getOptionalResourceTagValue(resource, config.getTagKeys().getAliases());
    }

    private List<ComponentTeam> getTeam(ResourceGroupsTaggingApiResource resource) {
        return getOptionalResourceTagValue(resource, config.getTagKeys().getTeam())
                .map(teamId -> List.of(ComponentTeam.builder().teamId(teamId).build()))
                .orElse(List.of());
    }

    private String getDescription(
            ResourceGroupsTaggingApiResource resource,
            AnalysedArn analysedArn,
            List<Alias> aliases
    ) {
        Optional<String> descriptionTag = getDescriptionTag(resource);

        if (descriptionTag.isPresent()) {
            return descriptionTag.get();
        } else if (config.getDetailedComponentDescriptions()) {
            return getDetailedComponentDescription(resource, analysedArn, aliases);
        } else {
            return "";
        }
    }

    private String getDetailedComponentDescription(
            ResourceGroupsTaggingApiResource resource,
            AnalysedArn analysedArn,
            List<Alias> aliases
    ) {
        StringBuilder builder = new StringBuilder()
                .append(analysedArn.getArn())
                .append("\n");

        if (!resource.getTags().isEmpty()) {
            builder.append("\nTags:\n\n");

            resource.getTags().forEach(tag -> builder.append("* ")
                    .append(tag.getKey())
                    .append("=")
                    .append(tag.getValue())
                    .append("\n")
            );

            builder.append("\nAliases:\n\n");

            aliases.forEach(alias -> builder.append("* ")
                    .append(alias.getId())
                    .append("\n")
            );
        }

        return builder.toString();
    }

    private Optional<String> getDescriptionTag(ResourceGroupsTaggingApiResource resource) {
        return getOptionalResourceTagValue(resource, config.getTagKeys().getDescription());
    }

    private Optional<String> getComponentTag(ResourceGroupsTaggingApiResource resource) {
        return getOptionalResourceTagValue(resource, config.getTagKeys().getComponent());
    }

    private String getEnvironmentId(ResourceGroupsTaggingApiResource resource, String environmentId) {
        return getOptionalResourceTagValue(resource, config.getTagKeys().getEnvironment())
                .orElse(environmentId);
    }
}
