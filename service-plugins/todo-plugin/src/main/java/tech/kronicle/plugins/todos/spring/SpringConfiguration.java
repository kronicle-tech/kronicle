package tech.kronicle.plugins.todos.spring;

import org.springframework.context.annotation.ComponentScan;
import tech.kronicle.plugins.todos.PluginPackage;

@ComponentScan(basePackageClasses = PluginPackage.class)
public class SpringConfiguration {

}
