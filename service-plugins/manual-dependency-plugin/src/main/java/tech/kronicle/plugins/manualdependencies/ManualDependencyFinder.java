package tech.kronicle.plugins.manualdependencies;

import org.pf4j.Extension;
import tech.kronicle.pluginapi.finders.TracingDataFinder;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.constants.DependencyTypeIds;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentDependency;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.sdk.models.DependencyDirection;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Extension
public class ManualDependencyFinder extends TracingDataFinder {

    private static final Duration CACHE_TTL = Duration.ZERO;

    @Override
    public String description() {
        return "Finds dependencies manually specified in kronicle.yaml files";
    }

    @Override
    public Output<TracingData, Void> find(ComponentMetadata componentMetadata) {
        return Output.ofOutput(
                TracingData.builder()
                        .dependencies(getDependencies(componentMetadata))
                        .build(),
                CACHE_TTL
        );
    }

    private List<Dependency> getDependencies(ComponentMetadata componentMetadata) {
        return Optional.ofNullable(componentMetadata).map(ComponentMetadata::getComponents).stream()
                .flatMap(Collection::stream)
                .flatMap(component -> component.getDependencies().stream()
                        .map(dependency -> createDependency(component, dependency)))
                .distinct()
                .collect(Collectors.toList());
    }

    private Dependency createDependency(Component component, ComponentDependency dependency) {
        String sourceComponentId;
        String targetComponentId;
        if (Objects.equals(dependency.getDirection(), DependencyDirection.INBOUND)) {
            sourceComponentId = dependency.getTargetComponentId();
            targetComponentId = component.getId();
        } else {
            sourceComponentId = component.getId();
            targetComponentId = dependency.getTargetComponentId();
        }
        return new Dependency(
                sourceComponentId,
                targetComponentId,
                Optional.ofNullable(dependency.getTypeId()).orElse(DependencyTypeIds.COMPOSITION),
                dependency.getLabel(),
                dependency.getDescription()
        );
    }
}
