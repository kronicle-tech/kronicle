package tech.kronicle.service.scanners.gradle.internal.services;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PropertyRetriever {

    public String getPropertyValue(String propertyName, Map<String, String> properties) {
        return properties.get(propertyName);
    }
}
