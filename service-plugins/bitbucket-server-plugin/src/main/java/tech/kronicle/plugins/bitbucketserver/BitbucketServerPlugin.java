package tech.kronicle.plugins.bitbucketserver;

import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import tech.kronicle.plugins.bitbucketserver.config.BitbucketServerConfig;
import tech.kronicle.plugins.bitbucketserver.spring.SpringConfiguration;
import tech.kronicle.pluginapi.KroniclePlugin;

public class BitbucketServerPlugin extends KroniclePlugin {

    public BitbucketServerPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public Class<?> getConfigType() {
        return BitbucketServerConfig.class;
    }

    @Override
    protected ApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
        applicationContext.register(SpringConfiguration.class);
        return applicationContext;
    }

}
