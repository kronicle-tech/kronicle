package tech.kronicle.pluginapi.finders;

import org.pf4j.ExtensionPoint;
import tech.kronicle.pluginapi.finders.models.ApiRepo;

import java.util.List;

public abstract class RepoFinder extends Finder<Void, List<ApiRepo>> {

    public abstract List<ApiRepo> find(Void ignored);
}
