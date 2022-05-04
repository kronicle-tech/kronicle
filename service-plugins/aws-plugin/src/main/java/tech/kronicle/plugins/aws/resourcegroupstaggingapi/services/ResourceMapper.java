package tech.kronicle.plugins.aws.resourcegroupstaggingapi.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.constants.TagKeys;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiTag;
import tech.kronicle.plugins.aws.utils.AnalysedArn;
import tech.kronicle.sdk.constants.DependencyTypeIds;
import tech.kronicle.sdk.models.Alias;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentDependency;
import tech.kronicle.sdk.models.ComponentState;
import tech.kronicle.sdk.models.ComponentTeam;
import tech.kronicle.sdk.models.DependencyDirection;
import tech.kronicle.sdk.models.EnvironmentState;
import tech.kronicle.sdk.models.Tag;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toUnmodifiableList;
import static tech.kronicle.common.CaseUtils.toKebabCase;
import static tech.kronicle.plugins.aws.resourcegroupstaggingapi.utils.ResourceUtils.getOptionalResourceTagValue;
import static tech.kronicle.plugins.aws.utils.ArnAnalyser.analyseArn;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ResourceMapper {

    private final AwsConfig config;

    public List<Component> mapResourcesToComponents(
            String environmentId,
            List<ResourceGroupsTaggingApiResource> resources
    ) {
        return resources.stream()
                .map(mapResourceToComponent(environmentId))
                .collect(Collectors.toList());
    }

    private Function<ResourceGroupsTaggingApiResource, Component> mapResourceToComponent(String environmentId) {
        return resource -> {
            AnalysedArn analysedArn = analyseArn(resource.getArn());
            Optional<String> nameTag = getNameTag(resource);
            String name = getName(nameTag, analysedArn);
            List<Alias> aliases = getAliases(analysedArn, name);
            return Component.builder()
                    .id(toKebabCase(analysedArn.getDerivedResourceType() + "-" + analysedArn.getResourceId()))
                    .aliases(aliases)
                    .name(name)
                    .typeId(toKebabCase(analysedArn.getDerivedResourceType()))
                    .tags(mapTags(resource))
                    .teams(getTeam(resource))
                    .description(getDescription(resource, analysedArn, aliases))
                    .platformId("aws-managed-service")
                    .dependencies(createDependencies(resource))
                    .state(
                            ComponentState.builder()
                                    .environments(List.of(
                                            EnvironmentState.builder()
                                                    .id(getEnvironmentId(resource, environmentId))
                                                    .build()
                                    ))
                                    .build()
                    )
                    .build();
        };
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

    private List<Alias> getAliases(AnalysedArn analysedArn, String name) {
        List<String> aliases = new ArrayList<>();
        aliases.add(analysedArn.getResourceId());
        aliases.add(analysedArn.getResourceId().toLowerCase());
        aliases.add(name);
        return aliases.stream()
                .map(alias -> Alias.builder().id(alias).build())
                .distinct()
                .collect(Collectors.toList());
    }

    private String getName(Optional<String> nameTag, AnalysedArn analysedArn) {
        return nameTag.orElseGet(analysedArn::getResourceId);
    }

    private Optional<String> getNameTag(ResourceGroupsTaggingApiResource resource) {
        return getOptionalResourceTagValue(resource, TagKeys.PASCAL_CASE_NAME);
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

    private List<ComponentDependency> createDependencies(ResourceGroupsTaggingApiResource resource) {
        if (config.getCreateDependenciesForResources()) {
            Optional<String> componentTag = getComponentTag(resource);
            return componentTag.map(this::createDependency).stream()
                    .collect(toUnmodifiableList());
        } else {
            return List.of();
        }
    }

    private ComponentDependency createDependency(String componentTag) {
        return ComponentDependency.builder()
                .targetComponentId(componentTag)
                .direction(DependencyDirection.INBOUND)
                .typeId(DependencyTypeIds.COMPOSITION)
                .label("is composed of")
                .build();
    }

    private Optional<String> getComponentTag(ResourceGroupsTaggingApiResource resource) {
        return getOptionalResourceTagValue(resource, config.getTagKeys().getComponent());
    }

    private String getEnvironmentId(ResourceGroupsTaggingApiResource resource, String environmentId) {
        return getOptionalResourceTagValue(resource, config.getTagKeys().getEnvironment())
                .orElse(environmentId);
    }
}
