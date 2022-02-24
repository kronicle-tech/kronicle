package tech.kronicle.plugins.manualdependencies;

import org.pf4j.Extension;
import tech.kronicle.componentmetadata.models.ComponentMetadata;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentDependency;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.sdk.models.DependencyDirection;
import tech.kronicle.pluginapi.finders.DependencyFinder;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Component
@Extension
public class ManualDependencyFinder extends DependencyFinder {
    @Override
    public String description() {
        return "Finds dependencies manually specified in kronicle.yaml files.  ";
    }

    @Override
    public List<Dependency> find(ComponentMetadata componentMetadata) {
        return Optional.ofNullable(componentMetadata).map(ComponentMetadata::getComponents).stream()
                .flatMap(Collection::stream)
                .flatMap(component -> component.getDependencies().stream()
                        .map(dependency -> createDependency(component, dependency)))
                .distinct()
                .collect(Collectors.toList());
    }

    private Dependency createDependency(Component component, ComponentDependency dependency) {
        if (Objects.equals(dependency.getDirection(), DependencyDirection.INBOUND)) {
            return new Dependency(dependency.getTargetComponentId(), component.getId());
        }
        return new Dependency(component.getId(), dependency.getTargetComponentId());
    }
}
