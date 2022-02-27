package tech.kronicle.plugins.github.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import tech.kronicle.plugins.github.config.GitHubConfig;

import java.net.http.HttpClient;

import static tech.kronicle.pluginutils.HttpClientFactory.createHttpClient;
import static tech.kronicle.pluginutils.JsonMapperFactory.createJsonMapper;

public class GuiceModule extends AbstractModule {

    @Provides
    public HttpClient httpClient(GitHubConfig config) {
        return createHttpClient(config.getTimeout());
    }

    @Provides
    public ObjectMapper objectMapper() {
        return createJsonMapper();
    }
}
