package tech.kronicle.plugins.gradle.internal.services;


import java.util.Map;

public class PropertyRetriever {

    public String getPropertyValue(String propertyName, Map<String, String> properties) {
        return properties.get(propertyName);
    }
}
