package tech.kronicle.plugins.gitlab.services;

import com.github.benmanes.caffeine.cache.CacheLoader;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.Nullable;
import tech.kronicle.plugins.gitlab.models.EnrichedGitLabRepo;

import javax.inject.Inject;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ReposCacheLoader implements CacheLoader<Object, List<EnrichedGitLabRepo>> {

    private final RepoFetcher fetcher;

    @Override
    public @Nullable List<EnrichedGitLabRepo> load(Object key) {
        return fetcher.getRepos();
    }
}
