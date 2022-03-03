package tech.kronicle.plugins.git.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import tech.kronicle.pluginapi.git.GitCloner;
import tech.kronicle.plugins.git.GitClonerImpl;
import tech.kronicle.pluginutils.ThrowableToScannerErrorMapper;

public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GitCloner.class).to(GitClonerImpl.class);
    }

    @Provides
    public ThrowableToScannerErrorMapper throwableToScannerErrorMapper() {
        return new ThrowableToScannerErrorMapper();
    }
}
