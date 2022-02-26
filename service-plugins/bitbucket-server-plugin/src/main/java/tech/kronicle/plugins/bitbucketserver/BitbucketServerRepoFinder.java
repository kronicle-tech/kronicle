package tech.kronicle.plugins.bitbucketserver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tech.kronicle.pluginapi.finders.RepoFinder;
import tech.kronicle.pluginapi.finders.models.ApiRepo;
import tech.kronicle.plugins.bitbucketserver.client.BitbucketServerClient;

import java.util.List;

@Component
@RequiredArgsConstructor
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
