package tech.kronicle.plugins.gradle.internal.services;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PropertyRetriever {

    public String getPropertyValue(String propertyName, Map<String, String> properties) {
        return properties.get(propertyName);
    }
}
