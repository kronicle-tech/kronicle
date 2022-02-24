package tech.kronicle.plugins.zipkin.spring;

import org.springframework.context.annotation.ComponentScan;
import tech.kronicle.plugins.zipkin.PluginPackage;

@ComponentScan(basePackageClasses = PluginPackage.class)
public class SpringConfiguration {

}
