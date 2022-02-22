package tech.kronicle.plugins.javaimports;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.pf4j.PluginWrapper;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.validation.annotation.Validated;
import tech.kronicle.plugins.javaimports.spring.SpringConfiguration;
import tech.kronicle.service.plugins.KroniclePlugin;

@Validated
@ConstructorBinding
@Value
@NonFinal
public class JavaImportPlugin extends KroniclePlugin {

    public JavaImportPlugin(PluginWrapper wrapper) {
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
