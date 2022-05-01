package tech.kronicle.plugins.gitlab;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.finders.RepoFinder;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.gitlab.services.CachingRepoFetcher;
import tech.kronicle.sdk.models.Repo;

import javax.inject.Inject;
import java.time.Duration;
import java.util.List;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class GitLabRepoFinder extends RepoFinder {

    private static final Duration CACHE_TTL = Duration.ZERO;

    private final CachingRepoFetcher fetcher;

    @Override
    public String description() {
        return "Find repositories hosted by GitLab.  ";
    }

    @Override
    public Output<List<Repo>, Void> find(Void ignored) {
        return Output.ofOutput(fetcher.getRepos(), CACHE_TTL);
    }

}
