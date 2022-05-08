package tech.kronicle.service.tests.sonarqube.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties("sonarqube-test")
@ConstructorBinding
@Value
public class SonarQubeTestConfig {

    List<String> expectedComponentTypeIds;
}
