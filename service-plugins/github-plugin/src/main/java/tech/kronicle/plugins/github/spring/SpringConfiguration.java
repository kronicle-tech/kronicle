package tech.kronicle.plugins.github.spring;

import org.springframework.context.annotation.ComponentScan;
import tech.kronicle.plugins.github.PluginPackage;

@ComponentScan(basePackageClasses = PluginPackage.class)
public class SpringConfiguration {

}
