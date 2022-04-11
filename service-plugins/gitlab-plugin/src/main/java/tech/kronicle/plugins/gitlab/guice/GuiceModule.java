package tech.kronicle.plugins.gitlab.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import tech.kronicle.plugins.gitlab.config.GitLabConfig;

import java.net.http.HttpClient;

import static tech.kronicle.utils.HttpClientFactory.createHttpClient;
import static tech.kronicle.utils.JsonMapperFactory.createJsonMapper;

public class GuiceModule extends AbstractModule {

    @Provides
    public HttpClient httpClient(GitLabConfig config) {
        return createHttpClient(config.getTimeout());
    }

    @Provides
    public ObjectMapper objectMapper() {
        return createJsonMapper()
                .registerModule(new JavaTimeModule());
    }
}
