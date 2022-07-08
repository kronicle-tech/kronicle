package tech.kronicle.plugins.kubernetes.guice;

import com.google.inject.AbstractModule;
import tech.kronicle.plugins.kubernetes.client.ApiClientFacade;
import tech.kronicle.plugins.kubernetes.client.ApiClientFacadeImpl;

public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ApiClientFacade.class).to(ApiClientFacadeImpl.class);
    }
}
