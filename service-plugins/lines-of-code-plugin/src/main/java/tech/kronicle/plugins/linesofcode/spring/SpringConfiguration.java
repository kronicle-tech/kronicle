package tech.kronicle.plugins.linesofcode.spring;

import org.springframework.context.annotation.ComponentScan;
import tech.kronicle.plugins.linesofcode.PluginPackage;

@ComponentScan(basePackageClasses = PluginPackage.class)
public class SpringConfiguration {

}
