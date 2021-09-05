package tech.kronicle.service.repoproviders.bitbucketserver;

import tech.kronicle.service.repoproviders.RepoProvider;
import tech.kronicle.service.repoproviders.bitbucketserver.client.BitbucketServerClient;
import tech.kronicle.service.models.ApiRepo;
import tech.kronicle.service.spring.stereotypes.Scanner;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Scanner
@RequiredArgsConstructor
public class BitbucketServerRepoProvider extends RepoProvider {

    private final BitbucketServerClient client;

    @Override
    public List<ApiRepo> getApiRepos() {
        return client.getNormalRepos();
    }
}
