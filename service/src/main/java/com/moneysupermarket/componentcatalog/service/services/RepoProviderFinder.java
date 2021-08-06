package com.moneysupermarket.componentcatalog.service.services;

import com.moneysupermarket.componentcatalog.service.repoproviders.RepoProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepoProviderFinder {

    private final List<RepoProvider> repoProviders;

    public List<RepoProvider> getRepoProviders() {
        return List.copyOf(repoProviders);
    }
}
