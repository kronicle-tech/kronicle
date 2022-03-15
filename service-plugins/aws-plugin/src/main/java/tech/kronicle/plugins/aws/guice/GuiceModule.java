package tech.kronicle.plugins.aws.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import java.time.Clock;

public class GuiceModule extends AbstractModule {

    @Provides
    public Clock clock() {
        return Clock.systemUTC();
    }
}
