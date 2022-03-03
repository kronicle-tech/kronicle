package tech.kronicle.plugins.bitbucketserver;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.finders.RepoFinder;
import tech.kronicle.pluginapi.finders.models.ApiRepo;
import tech.kronicle.plugins.bitbucketserver.client.BitbucketServerClient;

import javax.inject.Inject;
import java.util.List;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class BitbucketServerRepoFinder extends RepoFinder {

    private final BitbucketServerClient client;

    @Override
    public String description() {
        return "Find repositories hosted by Bitbucket Server.  ";
    }

    @Override
    public List<ApiRepo> find(Void ignored) {
        return client.getNormalRepos();
    }
}
