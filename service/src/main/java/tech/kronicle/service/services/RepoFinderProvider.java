package tech.kronicle.service.services;

import tech.kronicle.service.repofinders.RepoFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepoFinderProvider {

    private final List<RepoFinder> repoFinders;

    public List<RepoFinder> getRepoFinders() {
        return List.copyOf(repoFinders);
    }
}
