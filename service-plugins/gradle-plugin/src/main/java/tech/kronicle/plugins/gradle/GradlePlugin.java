package tech.kronicle.plugins.gradle;

import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import tech.kronicle.service.plugins.KroniclePlugin;
import tech.kronicle.plugins.gradle.config.GradleConfig;
import tech.kronicle.plugins.gradle.internal.spring.SpringConfiguration;

public class GradlePlugin extends KroniclePlugin {

    public GradlePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public Class<?> getConfigType() {
        return GradleConfig.class;
    }

    @Override
    protected ApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
//        applicationContext.setParent(parentApplicationContext);
        applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
//        applicationContext.setEnvironment(parentApplicationContext2.getEnvironment());
        applicationContext.register(SpringConfiguration.class);
        return applicationContext;
    }

}
