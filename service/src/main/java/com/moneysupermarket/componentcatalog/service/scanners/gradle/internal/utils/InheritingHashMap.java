package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.utils;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
public class InheritingHashMap<K, V> extends HashMap<K, V> {

    private final Map<K, V> parent;

    public InheritingHashMap() {
        this.parent = null;
    }

    @Override
    public V get(Object key) {
        V value = super.get(key);

        if (isNull(value) && nonNull(parent)) {
            value = parent.get(key);
        }

        return value;
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        V value = get(key);
        return nonNull(value) ? value : defaultValue;
    }
}
