package tech.kronicle.plugins.datadog.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import tech.kronicle.plugins.datadog.config.DatadogConfig;
import tech.kronicle.plugins.datadog.dependencies.config.DatadogDependenciesConfig;

import java.net.http.HttpClient;

import static tech.kronicle.pluginutils.HttpClientFactory.createHttpClient;
import static tech.kronicle.pluginutils.JsonMapperFactory.createJsonMapper;

public class GuiceModule extends AbstractModule {

    @Provides
    public DatadogDependenciesConfig datadogDependenciesConfig(DatadogConfig datadogConfig) {
        return datadogConfig.getDependencies();
    }

    @Provides
    public HttpClient httpClient(DatadogConfig config) {
        return createHttpClient(config.getTimeout());
    }

    @Provides
    public ObjectMapper objectMapper() {
        return createJsonMapper();
    }
}
