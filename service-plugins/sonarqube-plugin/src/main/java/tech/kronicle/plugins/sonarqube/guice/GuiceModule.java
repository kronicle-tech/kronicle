package tech.kronicle.plugins.sonarqube.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import tech.kronicle.plugins.sonarqube.config.SonarQubeConfig;
import tech.kronicle.utils.FileUtils;

import java.net.http.HttpClient;

import static tech.kronicle.utils.FileUtilsFactory.createFileUtils;
import static tech.kronicle.utils.HttpClientFactory.createHttpClient;
import static tech.kronicle.utils.JsonMapperFactory.createJsonMapper;

public class GuiceModule extends AbstractModule {

    @Provides
    public HttpClient httpClient(SonarQubeConfig config) {
        return createHttpClient(config.getTimeout());
    }

    @Provides
    public ObjectMapper objectMapper() {
        return createJsonMapper();
    }

    @Provides
    public FileUtils fileUtils() {
        return createFileUtils();
    }
}
