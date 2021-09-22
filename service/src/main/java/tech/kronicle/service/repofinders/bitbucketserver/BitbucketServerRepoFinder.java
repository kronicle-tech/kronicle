package tech.kronicle.service.repofinders.bitbucketserver;

import tech.kronicle.service.repofinders.RepoFinder;
import tech.kronicle.service.repofinders.bitbucketserver.client.BitbucketServerClient;
import tech.kronicle.service.models.ApiRepo;
import lombok.RequiredArgsConstructor;

import java.util.List;

@tech.kronicle.service.spring.stereotypes.RepoFinder
@RequiredArgsConstructor
public class BitbucketServerRepoFinder extends RepoFinder {

    private final BitbucketServerClient client;

    @Override
    public List<ApiRepo> findApiRepos() {
        return client.getNormalRepos();
    }
}
