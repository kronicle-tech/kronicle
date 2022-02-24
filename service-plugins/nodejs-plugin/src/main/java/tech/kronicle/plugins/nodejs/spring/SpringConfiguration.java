package tech.kronicle.plugins.nodejs.spring;

import org.springframework.context.annotation.ComponentScan;
import tech.kronicle.plugins.nodejs.PluginPackage;

@ComponentScan(basePackageClasses = PluginPackage.class)
public class SpringConfiguration {

}
