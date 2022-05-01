package tech.kronicle.pluginapi.finders;

import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.Repo;

import java.util.List;

public abstract class RepoFinder extends Finder<Void, List<Repo>> {

    public abstract Output<List<Repo>, Void> find(Void ignored);
}
