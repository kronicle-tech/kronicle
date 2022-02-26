package tech.kronicle.service.services;

import org.pf4j.PluginManager;
import org.springframework.stereotype.Service;
import tech.kronicle.pluginapi.finders.DependencyFinder;
import tech.kronicle.pluginapi.finders.Finder;
import tech.kronicle.pluginapi.finders.RepoFinder;

import java.util.List;

@Service
public class RepoFinderExtensionRegistry extends BaseExtensionRegistry<RepoFinder> {

    public RepoFinderExtensionRegistry(PluginManager pluginManager) {
        super(pluginManager);
    }

    @Override
    protected Class<RepoFinder> getItemType() {
        return RepoFinder.class;
    }

    public List<DependencyFinder> getDependencyFinders() {
        return getItems(DependencyFinder.class);
    }
}
