package tech.kronicle.plugins.datadog.dependencies.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.plugins.datadog.config.DatadogConfig;
import tech.kronicle.plugins.datadog.constants.DatadogApiPaths;
import tech.kronicle.plugins.datadog.constants.DatadogHttpHeaderNames;
import tech.kronicle.plugins.datadog.dependencies.models.ServiceDependenciesResponse;
import tech.kronicle.plugins.datadog.dependencies.models.ServiceWithDependencies;
import tech.kronicle.sdk.constants.DependencyTypeIds;
import tech.kronicle.utils.UriVariablesBuilder;
import tech.kronicle.sdk.models.Dependency;

import javax.inject.Inject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static tech.kronicle.utils.HttpClientFactory.createHttpRequestBuilder;
import static tech.kronicle.utils.UriTemplateUtils.expandUriTemplate;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DatadogDependencyClient {

  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final DatadogConfig config;

  public List<Dependency> getDependencies(String environment) {
    return createDependencies(getRawDependencies(environment));
  }

  @SneakyThrows
  private ServiceDependenciesResponse getRawDependencies(String environment) {
    Map<String, String> uriTemplateVariables = UriVariablesBuilder.builder()
            .addUriVariable("environment", environment)
            .build();
    String uri = expandUriTemplate(config.getBaseUrl() + DatadogApiPaths.SERVICE_DEPENDENCIES, uriTemplateVariables);
    logWebCall(uri);
    HttpRequest request = createHttpRequestBuilder(config.getTimeout())
            .uri(URI.create(uri))
            .header(DatadogHttpHeaderNames.API_KEY, config.getApiKey())
            .header(DatadogHttpHeaderNames.APPLICATION_KEY, config.getApplicationKey())
            .build();
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    return objectMapper.readValue(response.body(), new TypeReference<>() { });
  }

  private void logWebCall(String uri) {
    if (log.isInfoEnabled()) {
      log.info("Calling {}", uri);
    }
  }

  private List<Dependency> createDependencies(ServiceDependenciesResponse serviceDependenciesResponse) {
    if (isNull(serviceDependenciesResponse)) {
      return List.of();
    }
    return serviceDependenciesResponse.getServices().entrySet().stream()
            .flatMap(entry -> entry.getValue().getCalls().stream().map(call -> createDependency(entry, call)))
            .collect(Collectors.toList());
  }

  private Dependency createDependency(Map.Entry<String, ServiceWithDependencies> entry, String call) {
    return new Dependency(
            entry.getKey(),
            call,
            DependencyTypeIds.TRACE,
            null,
            null
    );
  }
}
