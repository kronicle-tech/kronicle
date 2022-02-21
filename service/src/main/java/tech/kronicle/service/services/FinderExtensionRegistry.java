package tech.kronicle.service.services;

import org.pf4j.PluginManager;
import org.springframework.stereotype.Service;
import tech.kronicle.service.finders.DependencyFinder;
import tech.kronicle.service.finders.Finder;

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

    public List<DependencyFinder> getDependencyFinders() {
        return getItems(DependencyFinder.class);
    }
}
