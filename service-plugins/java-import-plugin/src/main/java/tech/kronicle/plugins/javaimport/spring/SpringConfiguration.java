package tech.kronicle.plugins.javaimport.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import tech.kronicle.plugins.javaimport.PluginPackage;
import tech.kronicle.pluginutils.utils.AntStyleIgnoreFileLoader;
import tech.kronicle.pluginutils.utils.FileUtils;

@ComponentScan(basePackageClasses = PluginPackage.class)
public class SpringConfiguration {

    @Bean
    public FileUtils fileUtils() {
        return new FileUtils(new AntStyleIgnoreFileLoader());
    }
}
