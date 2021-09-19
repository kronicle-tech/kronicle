package tech.kronicle.service.repoproviders;

import tech.kronicle.service.models.ApiRepo;

import java.util.List;

public abstract class RepoProvider {

    public abstract List<ApiRepo> getApiRepos();
}
