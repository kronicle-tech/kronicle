package tech.kronicle.plugins.example.spring;

import org.springframework.context.annotation.ComponentScan;
import tech.kronicle.plugins.example.PluginPackage;

@ComponentScan(basePackageClasses = PluginPackage.class)
public class SpringConfiguration {

}
