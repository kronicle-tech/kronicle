package tech.kronicle.plugins.gitlab;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.finders.RepoFinder;
import tech.kronicle.plugins.gitlab.services.CachingRepoFetcher;
import tech.kronicle.sdk.models.Repo;

import javax.inject.Inject;
import java.util.List;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class GitLabRepoFinder extends RepoFinder {

    private final CachingRepoFetcher fetcher;

    @Override
    public String description() {
        return "Find repositories hosted by GitLab.  ";
    }

    @Override
    public List<Repo> find(Void ignored) {
        return fetcher.getRepos();
    }

}
