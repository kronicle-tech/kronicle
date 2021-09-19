package tech.kronicle.service.repofinders.bitbucketserver.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import tech.kronicle.service.testutils.TestFileHelper;

import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class BitbucketServerWireMockFactory {

    public static final int PORT = 36201;

    public static WireMockServer create() {
        return create(wireMockServer -> {
            IntStream.range(1, 3).forEach(serverNumber -> {
                String baseUrl = "http://localhost:" + PORT + "/server-" + serverNumber;
                wireMockServer.stubFor(get(urlPathEqualTo("/server-" + serverNumber + "/rest/api/1.0/repos"))
                        .withBasicAuth("test-username-" + serverNumber, "test-password-" + serverNumber)
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(replaceVars(
                                        readTestFile("bitbucket-server-api-responses/repos.json"),
                                        baseUrl))));
                wireMockServer.stubFor(get(urlPathEqualTo("/server-" + serverNumber + "/rest/api/1.0/repos"))
                        .withQueryParam("start", equalTo("2"))
                        .withBasicAuth("test-username-" + serverNumber, "test-password-" + serverNumber)
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(replaceVars(readTestFile("bitbucket-server-api-responses/repos-start-2.json"), baseUrl))));
                IntStream.range(1, 5).forEach(repoNumber -> {
                    MappingBuilder mappingBuilder = get(urlPathEqualTo(
                            "/server-" + serverNumber + "/rest/api/1.0/projects/EXAMPLE-PROJECT-" + repoNumber + "/repos/example-repo-" + repoNumber
                                    + "/browse/component-metadata.yaml"))
                            .withQueryParam("type", equalTo("true"))
                            .withBasicAuth("test-username-" + serverNumber, "test-password-" + serverNumber);
                    if (isOddNumber(repoNumber)) {
                        mappingBuilder.willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(readTestFile("bitbucket-server-api-responses/browse-file.json")));
                    } else {
                        mappingBuilder.willReturn(aResponse()
                                .withStatus(404)
                                .withHeader("Content-Type", "application/json")
                                .withBody(readTestFile("bitbucket-server-api-responses/browse-404.json")));
                    }
                    wireMockServer.stubFor(mappingBuilder);
                });
            });
            wireMockServer.stubFor(get(urlPathEqualTo("/server-does-not-exist/rest/api/1.0/repos"))
                    .withBasicAuth("test-username-1", "test-password-1")
                    .willReturn(aResponse()
                            .withStatus(404)
                            .withHeader("Content-Type", "text/plain")
                            .withBody("Server does not exist")));
        });
    }

    private static boolean isOddNumber(int serverNumber) {
        return serverNumber % 2 == 1;
    }

    private static String replaceVars(String value, String baseUrl) {
        return value.replaceAll("\\{\\{baseUrl}}", baseUrl);
    }

    private static String readTestFile(String name) {
        return TestFileHelper.readTestFile(name, BitbucketServerWireMockFactory.class);
    }

    private static WireMockServer create(Consumer<WireMockServer> initializer) {
        WireMockServer wireMockServer = new WireMockServer(PORT);
        initializer.accept(wireMockServer);
        wireMockServer.start();
        return wireMockServer;
    }
}
