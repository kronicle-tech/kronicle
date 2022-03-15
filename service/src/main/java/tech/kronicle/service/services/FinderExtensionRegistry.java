package tech.kronicle.service.services;

import org.pf4j.PluginManager;
import org.springframework.stereotype.Service;
import tech.kronicle.pluginapi.finders.ComponentFinder;
import tech.kronicle.pluginapi.finders.DependencyFinder;
import tech.kronicle.pluginapi.finders.Finder;
import tech.kronicle.pluginapi.finders.RepoFinder;

import java.util.Collection;
import java.util.List;

@Service
public class FinderExtensionRegistry extends BaseExtensionRegistry<Finder> {

    public FinderExtensionRegistry(PluginManager pluginManager) {
        super(pluginManager);
    }

    @Override
    protected Class<Finder> getItemType() {
        return Finder.class;
    }

    public List<RepoFinder> getRepoFinders() {
        return getItems(RepoFinder.class);
    }

    public List<DependencyFinder> getDependencyFinders() {
        return getItems(DependencyFinder.class);
    }

    public List<ComponentFinder> getComponentFinders() {
        return getItems(ComponentFinder.class);
    }
}
