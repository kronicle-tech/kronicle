package tech.kronicle.plugins.aws.resourcegroupstaggingapi.services;

import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiTag;
import tech.kronicle.plugins.aws.utils.AnalysedArn;
import tech.kronicle.sdk.models.Alias;
import tech.kronicle.sdk.models.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static tech.kronicle.common.CaseUtils.toKebabCase;
import static tech.kronicle.plugins.aws.utils.ArnAnalyser.analyseArn;

public class ResourceMapper {

    private final boolean detailedComponentDescriptions;

    @Inject
    public ResourceMapper(AwsConfig config) {
        this.detailedComponentDescriptions = Optional.ofNullable(config.getDetailedComponentDescriptions()).orElse(false);
    }

    public List<Component> mapResources(List<ResourceGroupsTaggingApiResource> resources) {
        return resources.stream()
                .map(this::mapResource)
                .collect(Collectors.toList());
    }

    private Component mapResource(ResourceGroupsTaggingApiResource resource) {
        AnalysedArn analysedArn = analyseArn(resource.getArn());
        Optional<String> nameTag = getNameTag(resource);
        List<Alias> aliases = getAliases(analysedArn);
        return Component.builder()
                .id(toKebabCase(analysedArn.getDerivedResourceType() + "-" + analysedArn.getResourceId()))
                .aliases(aliases)
                .name(getName(nameTag, analysedArn))
                .typeId(toKebabCase(analysedArn.getDerivedResourceType()))
                .description(getDescription(resource, analysedArn, aliases))
                .build();
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
        return getTagValue(resource, "Name");
    }

    private String getDescription(ResourceGroupsTaggingApiResource resource, AnalysedArn analysedArn, List<Alias> aliases) {
        if (!detailedComponentDescriptions) {
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

    private Optional<String> getTagValue(ResourceGroupsTaggingApiResource resource, String name) {
        return resource.getTags().stream()
                .filter(tag -> tag.getKey().equals(name))
                .findFirst()
                .map(ResourceGroupsTaggingApiTag::getValue);
    }
}
