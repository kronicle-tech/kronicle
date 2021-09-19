package tech.kronicle.service.repofinders.bitbucketserver;

import tech.kronicle.service.repofinders.RepoFinder;
import tech.kronicle.service.repofinders.bitbucketserver.client.BitbucketServerClient;
import tech.kronicle.service.models.ApiRepo;
import tech.kronicle.service.spring.stereotypes.Scanner;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Scanner
@RequiredArgsConstructor
public class BitbucketServerRepoFinder extends RepoFinder {

    private final BitbucketServerClient client;

    @Override
    public List<ApiRepo> getApiRepos() {
        return client.getNormalRepos();
    }
}
