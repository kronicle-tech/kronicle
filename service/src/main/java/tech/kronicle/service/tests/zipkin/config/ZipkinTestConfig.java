package tech.kronicle.service.tests.zipkin.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties("zipkin-test")
@ConstructorBinding
@Value
@NonFinal
public class ZipkinTestConfig {

    List<String> expectedComponentTypeIds;
}
