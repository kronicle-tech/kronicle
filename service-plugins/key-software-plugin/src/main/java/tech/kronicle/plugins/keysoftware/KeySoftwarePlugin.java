package tech.kronicle.plugins.keysoftware;

import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import tech.kronicle.pluginapi.KroniclePlugin;
import tech.kronicle.plugins.keysoftware.config.KeySoftwareConfig;
import tech.kronicle.plugins.keysoftware.spring.SpringConfiguration;

public class KeySoftwarePlugin extends KroniclePlugin {

    public KeySoftwarePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public Class<?> getConfigType() {
        return KeySoftwareConfig.class;
    }

    @Override
    protected ApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
        applicationContext.register(SpringConfiguration.class);
        return applicationContext;
    }

}
