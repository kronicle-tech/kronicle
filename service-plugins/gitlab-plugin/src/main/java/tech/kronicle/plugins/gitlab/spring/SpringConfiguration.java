package tech.kronicle.plugins.gitlab.spring;

import org.springframework.context.annotation.ComponentScan;
import tech.kronicle.plugins.gitlab.PluginPackage;

@ComponentScan(basePackageClasses = PluginPackage.class)
public class SpringConfiguration {

}
