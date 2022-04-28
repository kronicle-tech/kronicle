package tech.kronicle.plugins.gitlab.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.gitlab.models.EnrichedGitLabRepo;
import tech.kronicle.plugins.gitlab.models.api.GitLabJob;
import tech.kronicle.sdk.models.Repo;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CachingRepoFetcher {
    
    Duration cacheTtl = Duration.ofHours(6);
    
    private final RepoFetcher fetcher;
    private final RepoMapper mapper;
    private final Clock clock;
    private List<EnrichedGitLabRepo> cachedRepos;
    private Instant cachedAt;

    public List<Repo> getRepos() {
        Instant now = clock.instant();
        if (cacheHasExpired(now)) {
            cachedRepos = fetcher.getRepos();
            cachedAt = now;
        }
        return cachedRepos.stream()
                .map(repo -> {
                    LocalDateTime repoStateRefreshedAt = LocalDateTime.now(clock);
                    List<GitLabJob> repoState = fetcher.getRepoState(repo);
                    return mapper.mapRepo(repo)
                            .withState(mapper.mapState(repoState, repoStateRefreshedAt));
                })
                .collect(toUnmodifiableList());
    }

    private boolean cacheHasExpired(Instant now) {
        return isNull(cachedAt) || cachedAt.minus(cacheTtl).isBefore(now);
    }
}
