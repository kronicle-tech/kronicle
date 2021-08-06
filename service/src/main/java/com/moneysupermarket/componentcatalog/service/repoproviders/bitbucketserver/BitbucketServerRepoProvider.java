package com.moneysupermarket.componentcatalog.service.repoproviders.bitbucketserver;

import com.moneysupermarket.componentcatalog.service.repoproviders.RepoProvider;
import com.moneysupermarket.componentcatalog.service.repoproviders.bitbucketserver.client.BitbucketServerClient;
import com.moneysupermarket.componentcatalog.service.models.ApiRepo;
import com.moneysupermarket.componentcatalog.service.spring.stereotypes.Scanner;
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
