package tech.kronicle.plugins.aws.xray.services;

import tech.kronicle.plugins.aws.xray.models.XRayDependency;
import tech.kronicle.sdk.models.Dependency;

import java.util.List;
import java.util.stream.Collectors;

import static tech.kronicle.common.CaseUtils.toKebabCase;

public class DependencyAssembler {

    public List<Dependency> assembleDependencies(List<XRayDependency> dependencies) {
        return dependencies.stream()
                .map(dependency -> Dependency.builder()
                                .sourceComponentId(getComponentId(dependency.getSourceServiceNames()))
                                .targetComponentId(getComponentId(dependency.getTargetServiceNames()))
                                .build()
                )
                .distinct()
                .collect(Collectors.toList());
    }

    private String getComponentId(List<String> serviceNames) {
        return toKebabCase(serviceNames.get(0));
    }
}
