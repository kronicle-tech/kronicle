package tech.kronicle.service.repofinders.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.kronicle.service.models.ApiRepo;
import tech.kronicle.service.repofinders.RepoFinder;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepoFinderService {

    private final List<RepoFinder> repoFinders;
    private final RepoFilterService repoFilterService;

    public List<ApiRepo> findApiRepos() {
        return repoFinders.stream()
                .map(RepoFinder::findApiRepos)
                .flatMap(Collection::stream)
                .distinct()
                .filter(repoFilterService::keepRepo)
                .collect(Collectors.toList());
    }
}