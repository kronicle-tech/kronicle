package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.Repo;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepoFinderService {

    private final FinderExtensionRegistry registry;
    private final ExtensionExecutor executor;
    private final RepoFilterService repoFilterService;

    public List<Repo> findRepos() {
        return registry.getRepoFinders().stream()
                .map(repoFinder -> executor.executeFinder(repoFinder, null))
                .filter(Output::success)
                .map(Output::getOutput)
                .flatMap(Collection::stream)
                .distinct()
                .filter(repoFilterService::keepRepo)
                .collect(Collectors.toList());
    }
}
