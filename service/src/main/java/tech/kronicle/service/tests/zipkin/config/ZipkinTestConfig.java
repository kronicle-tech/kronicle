package tech.kronicle.service.tests.zipkin.config;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties("zipkin-test")
@Value
public class ZipkinTestConfig {

    List<String> expectedComponentTypeIds;
}
