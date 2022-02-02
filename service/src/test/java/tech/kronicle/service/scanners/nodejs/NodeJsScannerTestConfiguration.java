package tech.kronicle.service.scanners.nodejs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import tech.kronicle.service.scanners.nodejs.internal.services.npm.NpmPackageExtractor;
import tech.kronicle.service.utils.AntStyleIgnoreFileLoader;
import tech.kronicle.service.utils.FileUtils;

@EnableAutoConfiguration
@ComponentScan()
public class NodeJsScannerTestConfiguration {

    @Bean
    public FileUtils fileUtils() {
        return new FileUtils(new AntStyleIgnoreFileLoader());
    }

    @Bean
    public NpmPackageExtractor npmPackageExtractor(FileUtils fileUtils, ObjectMapper objectMapper) {
        return new NpmPackageExtractor(fileUtils, objectMapper);
    }
}
