package tech.kronicle.plugins.aws.xray.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.sdk.models.Dependency;

import javax.inject.Inject;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DependencyService {

    private final XRayServiceGraphFetcher fetcher;
    private final DependencyAssembler assembler;

    public List<Dependency> getDependencies() {
        return assembler.assembleDependencies(fetcher.getServiceGraph());
    }
}
