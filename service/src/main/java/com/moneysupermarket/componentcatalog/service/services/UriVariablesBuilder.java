package com.moneysupermarket.componentcatalog.service.services;

import java.util.HashMap;
import java.util.Map;

public class UriVariablesBuilder {

    private final Map<String, String> uriVariables = new HashMap<>();

    private UriVariablesBuilder() {
    }

    public static UriVariablesBuilder builder() {
        return new UriVariablesBuilder();
    }

    public UriVariablesBuilder addUriVariable(String name, Object value) {
        if (uriVariables.containsKey(name)) {
            throw new UnsupportedOperationException(String.format("Name %s already exists", name));
        }

        uriVariables.put(name, value.toString());
        return this;
    }

    public Map<String, String> build() {
        return Map.copyOf(uriVariables);
    }
}
