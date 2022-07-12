package tech.kronicle.plugins.kubernetes.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import tech.kronicle.plugins.kubernetes.client.ApiClientFacade;
import tech.kronicle.plugins.kubernetes.client.ApiClientFacadeImpl;

import java.time.Clock;

public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ApiClientFacade.class).to(ApiClientFacadeImpl.class);
    }

    @Provides
    public Clock clock() {
        return Clock.systemUTC();
    }
}
