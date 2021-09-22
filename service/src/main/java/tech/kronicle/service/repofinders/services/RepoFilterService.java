package tech.kronicle.service.repofinders.services;

import org.springframework.stereotype.Service;
import tech.kronicle.service.models.ApiRepo;
import tech.kronicle.service.repofinders.config.RepoFindersConfig;
import tech.kronicle.service.repofinders.config.RepoFindersIgnoredRepoConfig;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RepoFilterService {

    private final Set<String> ignoredRepoUrls;

    public RepoFilterService(RepoFindersConfig config) {
        ignoredRepoUrls = Optional.ofNullable(config)
                .map(RepoFindersConfig::getIgnoredRepos)
                .stream()
                .flatMap(Collection::stream)
                .map(RepoFindersIgnoredRepoConfig::getUrl)
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean keepRepo(ApiRepo repo) {
        return !ignoredRepoUrls.contains(repo.getUrl());
    }
}
