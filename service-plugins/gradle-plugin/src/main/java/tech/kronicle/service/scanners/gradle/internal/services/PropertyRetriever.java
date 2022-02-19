package tech.kronicle.service.scanners.gradle.internal.services;

import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class PropertyRetriever {

    public String getPropertyValue(String propertyName, Map<String, String> properties) {
        return properties.get(propertyName);
    }
}
