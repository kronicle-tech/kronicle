package tech.kronicle.pluginapi.finders;

import tech.kronicle.sdk.models.Repo;

import java.util.List;

public abstract class RepoFinder extends Finder<Void, List<Repo>> {

    public abstract List<Repo> find(Void ignored);
}
