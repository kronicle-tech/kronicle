package tech.kronicle.service.services;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginManager;
import tech.kronicle.service.models.ExtensionPointWithId;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseExtensionRegistry<T extends ExtensionPointWithId> {

    private final List<T> items;

    public BaseExtensionRegistry(PluginManager pluginManager) {
        this.items = getItemsFromPluginManager(pluginManager);
        if (log.isInfoEnabled()) {
            log.info(
                    "{} found: {}",
                    this.getClass().getSimpleName(),
                    getCommaSeparatedItemIds()
            );
        }
    }

    protected abstract Class<T> getItemType();

    private List<T> getItemsFromPluginManager(PluginManager pluginManager) {
        return pluginManager.getExtensions(getItemType());
    }

    private String getCommaSeparatedItemIds() {
        return items.stream()
                .map(T::id)
                .collect(Collectors.joining(", "));
    }

    public List<T> getAllItems() {
        return List.copyOf(items);
    }

    public T getItem(String id) {
        return items.stream()
                .filter(item -> Objects.equals(item.id(), id))
                .findFirst().orElse(null);
    }

    protected <U extends T> List<U> getItems(Class<U> clazz) {
        return items.stream()
                .filter(item -> clazz.isAssignableFrom(item.getClass()))
                .map(item -> (U) item)
                .collect(Collectors.toList());
    }
}
