package tech.kronicle.service.datadog.dependencies.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ServiceDependenciesResponse {

    private final Map<String, ServiceWithDependencies> mutableServices = new HashMap<>();
    private final Map<String, ServiceWithDependencies> services = Collections.unmodifiableMap(mutableServices);

    @JsonAnySetter
    public void addService(String serviceName, ServiceWithDependencies service) {
        mutableServices.put(serviceName, service);
    }

    public Map<String, ServiceWithDependencies> getServices() {
        return services;
    }
}
