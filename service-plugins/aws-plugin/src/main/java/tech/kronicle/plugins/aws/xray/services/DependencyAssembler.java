package tech.kronicle.plugins.aws.xray.services;

import tech.kronicle.plugins.aws.xray.models.Service;
import tech.kronicle.sdk.models.Dependency;

import java.util.List;
import java.util.stream.Collectors;

import static tech.kronicle.common.CaseUtils.toKebabCase;

public class DependencyAssembler {

    public List<Dependency> assembleDependencies(List<Service> services) {
        return services.stream()
                .flatMap(service -> service.getEdges().stream()
                        .map(edge -> Dependency.builder()
                                .sourceComponentId(toKebabCase(service.getName()))
                                .targetComponentId(toKebabCase(edge.getAliases().get(0).getName()))
                                .build()
                        )
                )
                .distinct()
                .collect(Collectors.toList());
    }
}
