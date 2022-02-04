package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BaseRegistry<T extends RegistryItem> {

    private final List<T> items;

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
