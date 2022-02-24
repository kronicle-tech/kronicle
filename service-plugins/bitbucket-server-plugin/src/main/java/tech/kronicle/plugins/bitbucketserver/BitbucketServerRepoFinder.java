package tech.kronicle.plugins.bitbucketserver;

import org.springframework.stereotype.Component;
import tech.kronicle.plugins.bitbucketserver.client.BitbucketServerClient;
import tech.kronicle.pluginapi.finders.RepoFinder;
import tech.kronicle.pluginapi.finders.models.ApiRepo;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BitbucketServerRepoFinder extends RepoFinder {

    private final BitbucketServerClient client;

    @Override
    public List<ApiRepo> findApiRepos() {
        return client.getNormalRepos();
    }
}
