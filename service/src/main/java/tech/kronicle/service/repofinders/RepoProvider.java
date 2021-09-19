package tech.kronicle.service.repofinders;

import tech.kronicle.service.models.ApiRepo;

import java.util.List;

public abstract class RepoProvider {

    public abstract List<ApiRepo> getApiRepos();
}
