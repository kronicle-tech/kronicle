package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.kronicle.sdk.models.Repo;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepoFinderService {

    private final FinderExtensionRegistry extensionRegistry;
    private final RepoFilterService repoFilterService;

    public List<Repo> findRepos() {
        return extensionRegistry.getRepoFinders().stream()
                .map(repoFinder -> repoFinder.find(null))
                .flatMap(Collection::stream)
                .distinct()
                .filter(repoFilterService::keepRepo)
                .collect(Collectors.toList());
    }
}
