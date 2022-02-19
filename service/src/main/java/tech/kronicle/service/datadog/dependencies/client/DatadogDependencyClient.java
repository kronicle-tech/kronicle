package tech.kronicle.service.datadog.dependencies.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.service.datadog.config.DatadogConfig;
import tech.kronicle.service.datadog.constants.DatadogApiPaths;
import tech.kronicle.service.datadog.constants.DatadogHttpHeaderNames;
import tech.kronicle.service.datadog.dependencies.config.DatadogDependenciesConfig;
import tech.kronicle.service.datadog.dependencies.models.ServiceDependenciesResponse;
import tech.kronicle.service.services.UriVariablesBuilder;
import tech.kronicle.service.spring.stereotypes.SpringComponent;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static tech.kronicle.service.utils.UriTemplateUtils.expandUriTemplate;

@SpringComponent
@Slf4j
@RequiredArgsConstructor
public class DatadogDependencyClient {

  private final WebClient webClient;
  private final DatadogConfig config;
  private final DatadogDependenciesConfig dependenciesConfig;

  public List<Dependency> getDependencies(String environment) {
    return createDependencies(getRawDependencies(environment));
  }

  private ServiceDependenciesResponse getRawDependencies(String environment) {
    Map<String, String> uriTemplateVariables = UriVariablesBuilder.builder()
            .addUriVariable("environment", environment)
            .build();
    logWebCall(DatadogApiPaths.SERVICE_DEPENDENCIES, uriTemplateVariables);
    return webClient.get().uri(config.getBaseUrl() + DatadogApiPaths.SERVICE_DEPENDENCIES, uriTemplateVariables)
            .header(DatadogHttpHeaderNames.API_KEY, config.getApiKey())
            .header(DatadogHttpHeaderNames.APPLICATION_KEY, config.getApplicationKey())
            .retrieve()
            .bodyToMono(ServiceDependenciesResponse.class)
            .block(dependenciesConfig.getTimeout());
  }

  private void logWebCall(String uriTemplate, Map<String, String> uriVariables) {
    if (log.isInfoEnabled()) {
      log.info("Calling {}", expandUriTemplate(uriTemplate, uriVariables));
    }
  }

  private List<Dependency> createDependencies(ServiceDependenciesResponse serviceDependenciesResponse) {
    if (isNull(serviceDependenciesResponse)) {
      return List.of();
    }
    return serviceDependenciesResponse.getServices().entrySet().stream()
            .flatMap(entry -> entry.getValue().getCalls().stream().map(call -> new Dependency(entry.getKey(), call)))
            .collect(Collectors.toList());
  }
}
