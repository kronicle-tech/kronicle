package tech.kronicle.service.repofinders;

import tech.kronicle.service.models.ApiRepo;

import java.util.List;

public abstract class RepoFinder {

    public abstract List<ApiRepo> findApiRepos();
}
