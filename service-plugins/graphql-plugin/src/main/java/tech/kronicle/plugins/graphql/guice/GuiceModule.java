package tech.kronicle.plugins.graphql.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import tech.kronicle.plugins.graphql.config.GraphQlConfig;
import tech.kronicle.utils.FileUtils;

import java.net.http.HttpClient;

import static tech.kronicle.utils.FileUtilsFactory.createFileUtils;
import static tech.kronicle.utils.HttpClientFactory.createHttpClient;

public class GuiceModule extends AbstractModule {

    @Provides
    public FileUtils fileUtils() {
        return createFileUtils();
    }

    @Provides
    public HttpClient httpClient(GraphQlConfig config) {
        return createHttpClient(config.getTimeout());
    }
}
