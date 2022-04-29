package tech.kronicle.plugins.gitlab.services;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import tech.kronicle.plugins.gitlab.config.GitLabConfig;
import tech.kronicle.plugins.gitlab.models.EnrichedGitLabRepo;
import tech.kronicle.plugins.gitlab.models.api.GitLabJob;
import tech.kronicle.sdk.models.Repo;

import javax.inject.Inject;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;

public class CachingRepoFetcher {

    static final Object REPOS_CACHE_KEY = new Object();

    private final RepoFetcher fetcher;
    private final RepoMapper mapper;
    private final Clock clock;
    private final LoadingCache<Object, List<EnrichedGitLabRepo>> reposCache;

    @Inject
    public CachingRepoFetcher(
            RepoFetcher fetcher,
            RepoMapper mapper,
            Clock clock,
            ReposCacheLoader reposCacheLoader,
            GitLabConfig config
    ) {
        this.fetcher = fetcher;
        this.mapper = mapper;
        this.clock = clock;
        this.reposCache = Caffeine.newBuilder()
                .refreshAfterWrite(config.getReposCacheTtl())
                .build(reposCacheLoader);
    }

    public List<Repo> getRepos() {
        List<EnrichedGitLabRepo> repos = reposCache.get(REPOS_CACHE_KEY);
        return repos.stream()
                .map(repo -> {
                    LocalDateTime repoStateRefreshedAt = LocalDateTime.now(clock);
                    List<GitLabJob> repoState = fetcher.getRepoState(repo);
                    return mapper.mapRepo(repo)
                            .withState(mapper.mapState(repoState, repoStateRefreshedAt));
                })
                .collect(toUnmodifiableList());
    }
}
