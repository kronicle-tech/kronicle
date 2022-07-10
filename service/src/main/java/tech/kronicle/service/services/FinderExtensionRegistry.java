package tech.kronicle.service.services;

import org.pf4j.PluginManager;
import org.springframework.stereotype.Service;
import tech.kronicle.pluginapi.finders.*;

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

    public List<ComponentFinder> getComponentFinders() {
        return getItems(ComponentFinder.class);
    }

    public List<TracingDataFinder> getTracingDataFinders() {
        return getItems(TracingDataFinder.class);
    }

    public List<DiagramFinder> getDiagramFinders() {
        return getItems(DiagramFinder.class);
    }
}
