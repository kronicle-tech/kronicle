package tech.kronicle.plugins.aws.resourcegroupstaggingapi.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.constants.TagKeys;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.utils.AnalysedArn;
import tech.kronicle.sdk.models.Alias;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentState;
import tech.kronicle.sdk.models.ComponentTeam;
import tech.kronicle.sdk.models.EnvironmentState;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            List<Alias> aliases = getAliases(analysedArn);
            return Component.builder()
                    .id(toKebabCase(analysedArn.getDerivedResourceType() + "-" + analysedArn.getResourceId()))
                    .aliases(aliases)
                    .name(getName(nameTag, analysedArn))
                    .typeId(toKebabCase(analysedArn.getDerivedResourceType()))
                    .teams(getTeam(resource))
                    .description(getDescription(resource, analysedArn, aliases))
                    .platformId("aws-managed-service")
                    .state(
                            ComponentState.builder()
                                    .environments(List.of(
                                            EnvironmentState.builder()
                                                    .id(environmentId)
                                                    .build()
                                    ))
                                    .build()
                    )
                    .build();
        };
    }

    private List<Alias> getAliases(AnalysedArn analysedArn) {
        List<String> aliases = new ArrayList<>();
        aliases.add(analysedArn.getResourceId());
        aliases.add(analysedArn.getResourceId().toLowerCase());
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

    private String getDescription(ResourceGroupsTaggingApiResource resource, AnalysedArn analysedArn, List<Alias> aliases) {
        if (!config.getDetailedComponentDescriptions()) {
            return "";
        }

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
                    .append(alias)
                    .append("\n")
            );
        }

        return builder.toString();
    }
}
