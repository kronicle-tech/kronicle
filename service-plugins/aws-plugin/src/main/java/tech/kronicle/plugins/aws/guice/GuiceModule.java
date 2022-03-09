package tech.kronicle.plugins.aws.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import software.amazon.awssdk.services.xray.XRayClient;

import java.time.Clock;

public class GuiceModule extends AbstractModule {

    @Provides
    public XRayClient xRayClient() {
        return XRayClient.create();
    }

    @Provides
    public Clock clock() {
        return Clock.systemUTC();
    }
}
