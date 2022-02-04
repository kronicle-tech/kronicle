package tech.kronicle.service.services;

import org.springframework.stereotype.Service;
import tech.kronicle.service.finders.DependencyFinder;
import tech.kronicle.service.finders.Finder;

import java.util.List;

@Service
public class FinderRegistry extends BaseRegistry<Finder<?>> {

    public FinderRegistry(List<Finder<?>> items) {
        super(items);
    }

    public List<DependencyFinder> getDependencyFinders() {
        return getItems(DependencyFinder.class);
    }
}
