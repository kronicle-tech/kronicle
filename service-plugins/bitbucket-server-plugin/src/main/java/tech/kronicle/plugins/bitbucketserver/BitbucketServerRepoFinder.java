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
    public List<ApiRepo> findApiRepos() {
        return client.getNormalRepos();
    }
}
