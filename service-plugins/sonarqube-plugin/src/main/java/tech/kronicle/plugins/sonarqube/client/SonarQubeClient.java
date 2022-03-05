package tech.kronicle.plugins.sonarqube.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Streams;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import tech.kronicle.plugins.sonarqube.config.SonarQubeConfig;
import tech.kronicle.plugins.sonarqube.constants.ApiPaths;
import tech.kronicle.plugins.sonarqube.constants.MetricKeys;
import tech.kronicle.plugins.sonarqube.models.Project;
import tech.kronicle.plugins.sonarqube.models.api.Component;
import tech.kronicle.plugins.sonarqube.models.api.ComponentQualifier;
import tech.kronicle.plugins.sonarqube.models.api.GetComponentMeasuresResponse;
import tech.kronicle.utils.HttpStatuses;
import tech.kronicle.sdk.models.sonarqube.SonarQubeMeasure;
import tech.kronicle.sdk.models.sonarqube.SummarySonarQubeMetric;

import javax.inject.Inject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static tech.kronicle.utils.HttpClientFactory.createHttpRequestBuilder;
import static tech.kronicle.utils.UriTemplateUtils.expandUriTemplate;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SonarQubeClient {

    private final HttpClient httpClient;
    private final SonarQubeConfig config;
    private final ObjectMapper objectMapper;

    public List<SummarySonarQubeMetric> getMetrics() {
        return getAllResourcePages(
                config.getBaseUrl() + ApiPaths.SEARCH_METRICS + "?p={pageNumber}",
                "Search Metrics",
                "metrics",
                new HashMap<>(),
                SummarySonarQubeMetric.class
        );
    }

    public List<Project> getProjects(String organization) {
        return getAllResourcePages(
                config.getBaseUrl() + ApiPaths.SEARCH_COMPONENTS + createProjectsUriTemplate(organization),
                "Search Components",
                "components",
                createProjectsUriVariables(organization),
                Component.class
        )
                .stream()
                .map(component -> new Project(component.getKey(), component.getName()))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public List<SonarQubeMeasure> getProjectMeasures(String projectKey, List<SummarySonarQubeMetric> metrics) {
        Response response = makeRequest(
                config.getBaseUrl() + ApiPaths.GET_COMPONENT_MEASURES + "?component={component}&metricKeys={metricKeys}",
                Map.ofEntries(
                        Map.entry("component", projectKey),
                        Map.entry("metricKeys", getMetricKeys(metrics))
                )
        );
        checkResponseStatus(response, "Get Component Measures");

        return objectMapper.readValue(response.getBody(), GetComponentMeasuresResponse.class)
                .getComponent()
                .getMeasures();
    }

    private String createProjectsUriTemplate(String organization) {
        StringBuilder builder = new StringBuilder("?qualifiers={qualifiers}&p={pageNumber}");
        if (nonNull(organization)) {
            builder.append("&organization={organization}");
        }
        return builder.toString();
    }

    private Map<String, String> createProjectsUriVariables(String organization) {
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("qualifiers", ComponentQualifier.TRK.toString());
        if (nonNull(organization)) {
            uriVariables.put("organization", organization);
        }
        return uriVariables;
    }

    private <T> List<T> getAllResourcePages(
            String uri,
            String endpointName,
            String itemsFieldName,
            Map<String, String> uriVariables,
            Class<T> type
    ) {
        int pageNumber = 1;
        List<T> allResources = new ArrayList<>();

        while (true) {
            List<T> page = getResourcePage(uri, endpointName, itemsFieldName, uriVariables, pageNumber, type);

            if (page.isEmpty()) {
                break;
            }

            allResources.addAll(page);
            pageNumber++;
        }

        return allResources;
    }

    @SneakyThrows
    private <T> List<T> getResourcePage(
            String uri,
            String endpointName,
            String itemsFieldName,
            Map<String, String> uriVariables,
            int pageNumber,
            Class<T> type
    ) {
        uriVariables.put("pageNumber", Integer.toString(pageNumber));
        Response response = makeRequest(uri, uriVariables);
        checkResponseStatus(response, endpointName);

        ObjectNode responseBody = (ObjectNode) objectMapper.readTree(response.getBody());
        ArrayNode itemJsons = (ArrayNode) responseBody.get(itemsFieldName);
        return Streams.stream(itemJsons.elements())
                .map(itemJson -> objectMapper.convertValue(itemJson, type))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private Response makeRequest(String uri, Map<String, String> uriVariables) {
        HttpRequest request = createHttpRequestBuilder(config.getTimeout())
                .uri(URI.create(expandUriTemplate(uri, uriVariables)))
                .build();
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );
        return new Response(response.statusCode(), response.body());
    }

    private void checkResponseStatus(Response response, String endpointName) {
        if (response.getStatusCode() != HttpStatuses.OK) {
            throw new SonarQubeClientException(endpointName, response.getStatusCode(), response.getBody());
        }
    }

    private String getMetricKeys(List<SummarySonarQubeMetric> metrics) {
        return metrics.stream()
                .map(SummarySonarQubeMetric::getKey)
                .filter(this::metricKeyIsNotAffectedBySonarQubeBug)
                .collect(Collectors.joining(","));
    }

    /**
     * This can be removed when supporting a minimum version of SonarQube 8.1 or higher.
     * See https://jira.sonarsource.com/browse/SONAR-12728 for more information.
     *
     * @param metricKey The metric key
     * @return A boolean indicating whether metric key is affected by the SonarQube bug.
     */
    private boolean metricKeyIsNotAffectedBySonarQubeBug(String metricKey) {
        return !MetricKeys.AFFECTED_BY_SONARQUBE_BUG.contains(metricKey);
    }

    @Value
    private static class Response {

        Integer statusCode;
        String body;
    }
}
