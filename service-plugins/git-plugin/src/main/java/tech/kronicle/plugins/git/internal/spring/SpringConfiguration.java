package tech.kronicle.plugins.git.internal.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import tech.kronicle.plugins.git.KronicleGitPluginPackage;
import tech.kronicle.service.scanners.services.ThrowableToScannerErrorMapper;

@ComponentScan(basePackageClasses = KronicleGitPluginPackage.class)
public class SpringConfiguration {

    @Bean
    public ThrowableToScannerErrorMapper throwableToScannerErrorMapper() {
        return new ThrowableToScannerErrorMapper();
    }

}
