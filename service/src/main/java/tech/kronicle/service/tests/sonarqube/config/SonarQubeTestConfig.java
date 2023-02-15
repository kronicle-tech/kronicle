package tech.kronicle.service.tests.sonarqube.config;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties("sonarqube-test")
@Value
public class SonarQubeTestConfig {

    List<String> expectedComponentTypeIds;
}
