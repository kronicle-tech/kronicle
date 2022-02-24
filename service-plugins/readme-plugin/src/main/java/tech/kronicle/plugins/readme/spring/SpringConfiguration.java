package tech.kronicle.plugins.readme.spring;

import org.springframework.context.annotation.ComponentScan;
import tech.kronicle.plugins.readme.PluginPackage;

@ComponentScan(basePackageClasses = PluginPackage.class)
public class SpringConfiguration {

}
