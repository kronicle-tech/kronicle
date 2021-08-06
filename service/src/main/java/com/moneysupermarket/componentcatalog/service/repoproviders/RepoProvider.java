package com.moneysupermarket.componentcatalog.service.repoproviders;

import com.moneysupermarket.componentcatalog.service.models.ApiRepo;

import java.util.List;

public abstract class RepoProvider {

    public abstract List<ApiRepo> getApiRepos();
}
