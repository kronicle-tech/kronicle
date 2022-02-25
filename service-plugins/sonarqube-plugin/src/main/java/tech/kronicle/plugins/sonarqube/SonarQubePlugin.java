package tech.kronicle.plugins.sonarqube;

import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import tech.kronicle.plugins.sonarqube.config.SonarQubeConfig;
import tech.kronicle.plugins.sonarqube.spring.SpringConfiguration;
import tech.kronicle.pluginapi.KroniclePlugin;

public class SonarQubePlugin extends KroniclePlugin {

    public SonarQubePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public Class<?> getConfigType() {
        return SonarQubeConfig.class;
    }

    @Override
    protected ApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
        applicationContext.register(SpringConfiguration.class);
        return applicationContext;
    }

}
