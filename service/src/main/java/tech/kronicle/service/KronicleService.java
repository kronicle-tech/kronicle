package tech.kronicle.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tech.kronicle.common.ValidationConstraintViolationTransformer;
import tech.kronicle.tracingprocessor.ComponentAliasResolver;
import tech.kronicle.tracingprocessor.TracingProcessor;
import tech.kronicle.tracingprocessor.TracingProcessorFactory;
import tech.kronicle.utils.FileUtils;
import tech.kronicle.utils.ThrowableToScannerErrorMapper;

import java.time.Clock;

import static tech.kronicle.tracingprocessor.ComponentAliasResolverFactory.createComponentAliasResolver;
import static tech.kronicle.tracingprocessor.TracingProcessorFactory.createTracingProcessor;
import static tech.kronicle.utils.FileUtilsFactory.createFileUtils;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class KronicleService {

    public static void main(String[] args) {
        SpringApplication.run(KronicleService.class, args);
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public FileUtils fileUtils() {
        return createFileUtils();
    }

    @Bean
    public ThrowableToScannerErrorMapper throwableToScannerErrorMapper() {
        return new ThrowableToScannerErrorMapper();
    }

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = new ObjectMapper();
        builder.configure(objectMapper);
        return objectMapper;
    }

    @Bean
    public YAMLMapper yamlMapper() {
        YAMLMapper yamlMapper = new YAMLMapper(new YAMLFactory());
        yamlMapper.configure(DeserializationFeature. FAIL_ON_UNKNOWN_PROPERTIES, true);
        return yamlMapper;
    }

    @Bean
    public TracingProcessor tracingProcessor() {
        return createTracingProcessor();
    }

    @Bean
    public ComponentAliasResolver componentAliasResolver() {
        return createComponentAliasResolver();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/v1/**").allowedOrigins("*");
            }
        };
    }

    @Bean
    public ValidationConstraintViolationTransformer validationConstraintViolationTransformer() {
        return new ValidationConstraintViolationTransformer();
    }
}
