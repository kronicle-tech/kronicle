package tech.kronicle.plugins.javaimports.spring;

import org.springframework.context.annotation.ComponentScan;
import tech.kronicle.plugins.javaimports.PluginPackage;

@ComponentScan(basePackageClasses = PluginPackage.class)
public class SpringConfiguration {

}
