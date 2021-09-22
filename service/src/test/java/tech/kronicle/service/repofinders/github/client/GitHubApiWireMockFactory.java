package tech.kronicle.service.repofinders.github.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import lombok.Getter;
import tech.kronicle.service.repofinders.github.constants.GitHubApiHeaders;
import tech.kronicle.service.testutils.TestFileHelper;

import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class GitHubApiWireMockFactory {

    public static final int PORT = 36208;

    public static WireMockServer create() {
        return create(wireMockServer -> {
            String baseUrl = "http://localhost:" + PORT;
            Stream.of(Scenario.values()).forEach(scenario -> stubUserSpecificResponses(wireMockServer, baseUrl, scenario));
        });
    }

    private static void stubUserSpecificResponses(WireMockServer wireMockServer, String baseUrl, Scenario scenario) {
        RequestNumber requestNumber = new RequestNumber();
        int userReposRequestNumber = requestNumber.getNext();
        MappingBuilder request = createUserReposRequest(scenario, userReposRequestNumber);
        if (scenario.allResourcesNotFound) {
            wireMockServer.stubFor(request
                    .willReturn(aResponse()
                            .withStatus(404)
                            .withHeader("Content-Type", "text/plain")
                            .withBody("User does not exist")));
        } else {
            wireMockServer.stubFor(request
                    .willReturn(createUserReposResponse(baseUrl, scenario, userReposRequestNumber)));
            IntStream.range(1, 5).forEach(repoNumber -> {
                int repoRootContentsRequestNumber = requestNumber.getNext();
                wireMockServer.stubFor(createRepoRootContentsRequest(scenario, repoNumber, repoRootContentsRequestNumber)
                        .willReturn(createRepoRootContentsResponse(scenario, repoNumber, repoRootContentsRequestNumber)));
            });
        }
    }

    private static MappingBuilder createUserReposRequest(Scenario scenario, int requestNumber) {
        MappingBuilder builder = get(urlPathEqualTo("/user/repos"))
                .withBasicAuth(scenario.username, "test-personal-access-token");
        if (scenario.eTag && scenario.userReposNotModified) {
            builder.withHeader("If-None-Match", equalTo(createRequestETag(requestNumber)));
        }
        return builder;
    }

    private static ResponseDefinitionBuilder createUserReposResponse(String baseUrl, Scenario scenario, int requestNumber) {
        ResponseDefinitionBuilder builder = aResponse();
        if (scenario.eTag && scenario.userReposNotModified) {
            builder.withStatus(304);
        } else {
            builder.withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(replaceVars(readTestFile("github-api-responses/user-repos.json"), baseUrl, scenario.username));
        }
        builder.withHeader("ETag", createResponseETag(scenario, requestNumber));
        if (scenario.rateLimitResponseHeaders) {
            createRateLimitResponseHeaders(builder, requestNumber);
        }
        return builder;
    }

    private static MappingBuilder createRepoRootContentsRequest(Scenario scenario, int repoNumber, int requestNumber) {
        MappingBuilder builder = get(urlPathEqualTo(
                "/repos/" + scenario.username + "/test-repo-" + repoNumber + "/contents/"))
                .withBasicAuth(scenario.username, "test-personal-access-token");
        if (scenario.eTag && scenario.repo2NotModified && repoNumber == 2) {
            builder.withHeader("If-None-Match", equalTo(createRequestETag(requestNumber)));
        }
        return builder;
    }

    private static ResponseDefinitionBuilder createRepoRootContentsResponse(Scenario scenario, int repoNumber, int requestNumber) {
        ResponseDefinitionBuilder builder = aResponse();
        if (scenario.eTag && scenario.repo2NotModified && repoNumber == 2) {
            builder.withStatus(304);
        } else {
            builder.withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(readTestFile(getRepoContentsFileName(repoNumber)));
        }
        builder.withHeader("ETag", createResponseETag(scenario, requestNumber));
        if (scenario.rateLimitResponseHeaders) {
            createRateLimitResponseHeaders(builder, requestNumber);
        }
        return builder;
    }

    private static String createRequestETag(int requestNumber) {
        return "test-etag-" + requestNumber;
    }

    private static String createResponseETag(Scenario scenario, int requestNumber) {
        if (shouldResponseBeModified(scenario, requestNumber)) {
            return "test-modified-etag-" + requestNumber;
        }
        return "test-etag-" + requestNumber;
    }

    private static boolean shouldResponseBeModified(Scenario scenario, int requestNumber) {
        return !((requestNumber == 1 && scenario.userReposNotModified) || (requestNumber == 3 && scenario.repo2NotModified));
    }

    private static String getRepoContentsFileName(int repoNumber) {
        switch (repoNumber) {
            case 1:
                return "github-api-responses/repo-with-kronicle-yaml-file.json";
            case 3:
                return "github-api-responses/repo-with-component-metadata-yaml-file.json";
            case 2:
            case 4:
                return "github-api-responses/repo-with-no-metadata-files.json";
            default:
                throw new RuntimeException("Unexpected repo number " + repoNumber);
        }
    }

    private static void createRateLimitResponseHeaders(ResponseDefinitionBuilder builder, int requestNumber) {
        int limit = 5_000 + requestNumber * 2;
        int used = 1_000 + requestNumber;
        int remaining = limit - used;
        int reset = 1577836800 + requestNumber;
        String resource = "test-resource-" + requestNumber;
        builder.withHeader(GitHubApiHeaders.RATE_LIMIT_LIMIT, Integer.toString(limit))
                .withHeader(GitHubApiHeaders.RATE_LIMIT_REMAINING, Integer.toString(remaining))
                .withHeader(GitHubApiHeaders.RATE_LIMIT_RESET, Integer.toString(reset))
                .withHeader(GitHubApiHeaders.RATE_LIMIT_USED, Integer.toString(used))
                .withHeader(GitHubApiHeaders.RATE_LIMIT_RESOURCE, resource);
    }

    private static String replaceVars(String value, String baseUrl, String username) {
        return value.replaceAll("\\{\\{baseUrl}}", baseUrl)
                .replaceAll("\\{\\{username}}", username);
    }

    private static String readTestFile(String name) {
        return TestFileHelper.readTestFile(name, GitHubApiWireMockFactory.class);
    }

    private static WireMockServer create(Consumer<WireMockServer> initializer) {
        WireMockServer wireMockServer = new WireMockServer(PORT);
        initializer.accept(wireMockServer);
        wireMockServer.start();
        return wireMockServer;
    }

    @Getter
    public enum Scenario {

        SIMPLE("simple", false, false, false, false, false), 
        RATE_LIMIT_RESPONSE_HEADERS("rate-limit-response-headers", false, true, false, false, false),
        ETAG_USER_REPOS_NOT_MODIFIED("etag-user-repos-not-modified", false, false, true, true, false),
        ETAG_REPO_2_NOT_MODIFIED("etag-repo-2-not-modified", false, false, true, false, true),
        NOT_FOUND("not-found", true, false, false, false, false);

        Scenario(String username, Boolean allResourcesNotFound, Boolean rateLimitResponseHeaders, Boolean eTag, Boolean userReposNotModified, Boolean repo2NotModified) {
            this.username = username;
            this.allResourcesNotFound = allResourcesNotFound;
            this.rateLimitResponseHeaders = rateLimitResponseHeaders;
            this.eTag = eTag;
            this.userReposNotModified = userReposNotModified;
            this.repo2NotModified = repo2NotModified;
        }

        private final String username;
        private final Boolean allResourcesNotFound;
        private final Boolean rateLimitResponseHeaders;
        private final Boolean eTag;
        private final Boolean userReposNotModified;
        private final Boolean repo2NotModified;
    }

    private static class RequestNumber {

        private int number;

        public int getNext() {
            number++;
            return number;
        }
    }
}
