package tech.kronicle.service.services;

import lombok.extern.slf4j.Slf4j;
import tech.kronicle.service.models.RegistryItem;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class BaseRegistry<T extends RegistryItem> {

    private final List<T> items;

    public BaseRegistry(List<T> items) {
        this.items = items;
        if (log.isInfoEnabled()) {
            log.info(
                    "{} found: {}",
                    this.getClass().getSimpleName(),
                    items.stream()
                            .map(T::id)
                            .collect(Collectors.joining(", "))
            );
        }
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
