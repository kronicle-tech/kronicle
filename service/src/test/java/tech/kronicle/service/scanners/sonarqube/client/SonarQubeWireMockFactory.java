package tech.kronicle.service.scanners.sonarqube.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static java.util.Objects.nonNull;

public class SonarQubeWireMockFactory {

    public static final int PORT = 36202;
    public static final String TEST_ORGANIZATION = "test-organization";

    private static final int PAGE_SIZE = 100;
    private static final int ITEM_COUNT = 105;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public WireMockServer createWithRealResponses() {
        return create(wireMockServer -> {
            IntStream.range(1, 4).forEach(pageNumber -> wireMockServer.stubFor(get(urlPathEqualTo("/api/metrics/search"))
                    .withQueryParam("p", equalTo(Integer.toString(pageNumber)))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(createMetricsBody(pageNumber, objectMapper)))));

            stubProjectRequests(wireMockServer, objectMapper, null);
            stubProjectRequests(wireMockServer, objectMapper, TEST_ORGANIZATION);

            wireMockServer.stubFor(get(urlPathEqualTo("/api/measures/component"))
                    .withQueryParam("component", equalTo("test-component-key-1"))
                    .withQueryParam("metricKeys", equalTo("test-metric-key-1,test-metric-key-2"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(createComponentMeasuresBody(objectMapper))));
        });
    }

    private void stubProjectRequests(WireMockServer wireMockServer, ObjectMapper objectMapper, String organization) {
        IntStream.range(1, 4).forEach(pageNumber -> {
            MappingBuilder requestBuilder = get(urlPathEqualTo("/api/components/search"))
                    .withQueryParam("qualifiers", equalTo("TRK"))
                    .withQueryParam("p", equalTo(Integer.toString(pageNumber)));
            if (nonNull(organization)) {
                requestBuilder.withQueryParam("organization", equalTo(organization));
            }
            wireMockServer.stubFor(requestBuilder
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(createComponentsBody(pageNumber, objectMapper, organization))));
        });
    }

    private String createMetricsBody(int pageNumber, ObjectMapper objectMapper) {
        ObjectNode metrics = objectMapper.createObjectNode();
        ArrayNode metricsArray = metrics.putArray("metrics");
        // Metrics are deliberately spread over two pages with a third page that is empty
        getItemNumbers(pageNumber).forEach(metricNumber -> {
            ObjectNode metric = objectMapper.createObjectNode();
            metric.put("id", Integer.toString(1000 + metricNumber));
            metric.put("key", "test-metric-key-" + metricNumber);
            metric.put("type", "INT");
            metric.put("name", "Test Metric Name " + metricNumber);
            metric.put("description", "Test Metric Description " + metricNumber);
            metric.put("domain", "Test Metric Domain " + metricNumber);
            metric.put("direction", metricNumber % 2 == 0 ? -1 : 1);
            metric.put("qualitative", metricNumber % 2 == 0);
            metric.put("hidden", false);
            metric.put("custom", false);
            metricsArray.add(metric);
        });
        metrics.put("total", SonarQubeWireMockFactory.ITEM_COUNT);
        metrics.put("p", pageNumber);
        metrics.put("ps", SonarQubeWireMockFactory.PAGE_SIZE);
        try {
            return objectMapper.writeValueAsString(metrics);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String createComponentsBody(int pageNumber, ObjectMapper objectMapper, String organization) {
        ObjectNode rootJson = objectMapper.createObjectNode();
        ObjectNode pagingJson = rootJson.putObject("paging");
        pagingJson.put("pageIndex", pageNumber);
        pagingJson.put("pageSize", SonarQubeWireMockFactory.PAGE_SIZE);
        pagingJson.put("total", SonarQubeWireMockFactory.ITEM_COUNT);
        ArrayNode componentsJson = rootJson.putArray("components");
        // Components are deliberately spread over two pages with a third page that is empty
        getItemNumbers(pageNumber).forEach(componentNumber -> {
            ObjectNode component = objectMapper.createObjectNode();
            component.put("organization", "test-organization-" + componentNumber);
            component.put("id", "test-component-id-" + (1000 + componentNumber));
            component.put("key", "test-component-key-" + componentNumber);
            component.put("name", createComponentName(componentNumber, organization));
            component.put("qualifier", "TRK");
            component.put("project", "test-project-" + componentNumber);
            componentsJson.add(component);
        });
        try {
            return objectMapper.writeValueAsString(rootJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String createComponentName(int componentNumber, String organization) {
        return "Test Component Name " + componentNumber + " with "
                + (nonNull(organization) ? "organization " + organization : "no organization");
    }

    private String createComponentMeasuresBody(ObjectMapper objectMapper) {
        ObjectNode rootJson = objectMapper.createObjectNode();
        ObjectNode componentJson = rootJson.putObject("component");
        componentJson.put("id", "test-component-id-1001");
        componentJson.put("id", "test-component-key-1");
        componentJson.put("name", "Test Component Name 1");
        componentJson.put("qualifier", "TRK");
        ArrayNode measuresJson = componentJson.putArray("measures");
        IntStream.range(1, 3).forEach(measureNumber -> {
            ObjectNode measureJson = measuresJson.addObject();
            measureJson.put("metric", "test-metric-key-" + measureNumber);
            measureJson.put("value", Integer.toString(measureNumber));
            measureJson.put("bestValue", false);
        });
        try {
            return objectMapper.writeValueAsString(rootJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private IntStream getItemNumbers(int pageNumber) {
        return IntStream.range(1 + ((pageNumber - 1) * PAGE_SIZE), Math.min(pageNumber * PAGE_SIZE, ITEM_COUNT) + 1);
    }

    private WireMockServer create(Consumer<WireMockServer> initializer) {
        WireMockServer wireMockServer = new WireMockServer(PORT);
        initializer.accept(wireMockServer);
        wireMockServer.start();
        return wireMockServer;
    }
}
