package tech.kronicle.plugins.bitbucketserver.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import tech.kronicle.plugins.bitbucketserver.config.BitbucketServerConfig;

import java.net.http.HttpClient;

import static tech.kronicle.utils.HttpClientFactory.createHttpClient;
import static tech.kronicle.utils.JsonMapperFactory.createJsonMapper;

public class GuiceModule extends AbstractModule {

    @Provides
    public HttpClient httpClient(BitbucketServerConfig config) {
        return createHttpClient(config.getTimeout());
    }

    @Provides
    public ObjectMapper objectMapper() {
        return createJsonMapper();
    }
}
