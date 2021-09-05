package tech.kronicle.service.services;

import tech.kronicle.service.repoproviders.RepoProvider;
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
