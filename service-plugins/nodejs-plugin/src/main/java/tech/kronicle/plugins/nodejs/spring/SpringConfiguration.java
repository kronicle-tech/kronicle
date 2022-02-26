package tech.kronicle.plugins.nodejs.spring;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import tech.kronicle.plugins.nodejs.PluginPackage;
import tech.kronicle.pluginutils.utils.AntStyleIgnoreFileLoader;
import tech.kronicle.pluginutils.utils.FileUtils;

@ComponentScan(basePackageClasses = PluginPackage.class)
public class SpringConfiguration {

    @Bean
    public FileUtils fileUtils() {
        return new FileUtils(new AntStyleIgnoreFileLoader());
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new JsonMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
