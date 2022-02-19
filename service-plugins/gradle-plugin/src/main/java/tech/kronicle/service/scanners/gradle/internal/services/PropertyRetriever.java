package tech.kronicle.service.scanners.gradle.internal.services;

import tech.kronicle.service.spring.stereotypes.SpringComponent;

import java.util.Map;

@SpringComponent
public class PropertyRetriever {

    public String getPropertyValue(String propertyName, Map<String, String> properties) {
        return properties.get(propertyName);
    }
}
