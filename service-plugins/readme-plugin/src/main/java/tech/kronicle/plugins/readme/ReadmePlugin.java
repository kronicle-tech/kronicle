package tech.kronicle.plugins.readme;

import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import tech.kronicle.pluginapi.KroniclePlugin;
import tech.kronicle.plugins.readme.spring.SpringConfiguration;

public class ReadmePlugin extends KroniclePlugin {

    public ReadmePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public Class<?> getConfigType() {
        return null;
    }

    @Override
    protected ApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
        applicationContext.register(SpringConfiguration.class);
        return applicationContext;
    }

}
