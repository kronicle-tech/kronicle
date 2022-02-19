package tech.kronicle.plugins.manualdependencies.spring;

import org.springframework.context.annotation.ComponentScan;
import tech.kronicle.plugins.manualdependencies.PluginPackage;

@ComponentScan(basePackageClasses = PluginPackage.class)
public class SpringConfiguration {

}
