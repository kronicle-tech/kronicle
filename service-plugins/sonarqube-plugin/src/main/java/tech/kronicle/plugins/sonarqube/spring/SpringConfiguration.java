package tech.kronicle.plugins.sonarqube.spring;

import org.springframework.context.annotation.ComponentScan;
import tech.kronicle.plugins.sonarqube.PluginPackage;

@ComponentScan(basePackageClasses = PluginPackage.class)
public class SpringConfiguration {

}
