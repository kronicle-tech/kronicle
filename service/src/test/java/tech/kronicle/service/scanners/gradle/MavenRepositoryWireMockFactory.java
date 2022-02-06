package tech.kronicle.service.scanners.gradle;

import com.github.tomakehurst.wiremock.WireMockServer;

import java.util.function.Consumer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class MavenRepositoryWireMockFactory {

    public static final int PORT = 36211;

    public WireMockServer create() {
        return create(this::stubResponses);
    }

    private void stubResponses(WireMockServer wireMockServer) {
        wireMockServer.stubFor(get(urlPathEqualTo("/repo-with-authentication/test/group/id/test-artifact-id/test-version/test-artifact-id-test-version.pom"))
                .withHeader("test-header-1", equalTo("test-value-1"))
                .withHeader("test-header-2", equalTo("test-value-2"))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                                "    <modelVersion>4.0.0</modelVersion>\n" +
                                "    <groupId>test.group.id</groupId>\n" +
                                "    <artifactId>test-artifact-id</artifactId>\n" +
                                "    <version>test-version</version>\n" +
                                "    <name>test.group.id:test-artifact-id</name>\n" +
                                "</project>")));
    }

    private WireMockServer create(Consumer<WireMockServer> initializer) {
        WireMockServer wireMockServer = new WireMockServer(PORT);
        initializer.accept(wireMockServer);
        wireMockServer.start();
        return wireMockServer;
    }
}
