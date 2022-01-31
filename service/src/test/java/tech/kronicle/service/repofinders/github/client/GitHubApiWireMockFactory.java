package tech.kronicle.service.repofinders.github.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import lombok.Builder;
import lombok.Value;
import tech.kronicle.service.repofinders.github.config.GitHubRepoFinderAccessTokenConfig;
import tech.kronicle.service.repofinders.github.constants.GitHubApiHeaders;
import tech.kronicle.service.testutils.TestFileHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Objects.nonNull;

public class GitHubApiWireMockFactory {

    public static final int PORT = 36208;

    public WireMockServer create(Scenario scenario) {
        return create(wireMockServer -> {
            String baseUrl = "http://localhost:" + PORT;
            stubScenarioSpecificResponses(wireMockServer, baseUrl, scenario);
        });
    }

    private void stubScenarioSpecificResponses(WireMockServer wireMockServer, String baseUrl, Scenario scenario) {
        RequestNumber requestNumber = new RequestNumber();
        int reposRequestNumber = requestNumber.getNext();
        MappingBuilder request = createReposRequest(scenario, reposRequestNumber);
        if (scenario.internalServerError) {
            wireMockServer.stubFor(request
                    .willReturn(aResponse()
                            .withStatus(500)
                            .withHeader("Content-Type", "text/plain")
                            .withBody("Internal Server Error")));
        } else if (scenario.repoListNotFound) {
            wireMockServer.stubFor(request
                    .willReturn(aResponse()
                            .withStatus(404)
                            .withHeader("Content-Type", "text/plain")
                            .withBody("Not Found")));
        } else {
            wireMockServer.stubFor(request
                    .willReturn(createRepoListResponse(baseUrl, scenario, reposRequestNumber)));
            IntStream.range(1, 5).forEach(repoNumber -> {
                int repoRootContentsRequestNumber = requestNumber.getNext();
                wireMockServer.stubFor(createRepoRootContentsRequest(scenario, repoNumber, repoRootContentsRequestNumber)
                        .willReturn(createRepoRootContentsResponse(scenario, repoNumber, repoRootContentsRequestNumber)));
            });
        }
    }

    private boolean isRepo3AndRepo3HasNoContent(Scenario scenario, int repoNumber) {
        return repoNumber == 3 && scenario.repo3NoContent;
    }

    private MappingBuilder createReposRequest(Scenario scenario, int requestNumber) {
        MappingBuilder builder = get(urlPathEqualTo(getReposUrl(scenario)));
        addBasicAuthIfNeeded(scenario, builder);
        if (scenario.eTag && scenario.repoListNotModified) {
            builder.withHeader("If-None-Match", equalTo(createRequestETag(requestNumber)));
        }
        return builder;
    }

    private String getReposUrl(Scenario scenario) {
        switch (scenario.reposResourceType) {
            case AUTHENTICATED_USER:
                return "/user/repos";
            case USER:
                return "/users/" + scenario.name + "/repos";
            case ORGANIZATION:
                return "/orgs/" + scenario.name + "/repos";
            default:
                throw new RuntimeException("Unexpected repos resource type " + scenario.reposResourceType);
        }
    }

    private ResponseDefinitionBuilder createRepoListResponse(String baseUrl, Scenario scenario, int requestNumber) {
        ResponseDefinitionBuilder builder = aResponse();
        if (scenario.eTag && scenario.repoListNotModified) {
            builder.withStatus(304);
        } else {
            builder.withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(replaceVars(readTestFile("github-api-responses/all-repos.json"), baseUrl, scenario.name));
        }
        builder.withHeader("ETag", createResponseETag(scenario, requestNumber));
        if (scenario.rateLimitResponseHeaders) {
            createRateLimitResponseHeaders(builder, requestNumber);
        }
        return builder;
    }

    private MappingBuilder createRepoRootContentsRequest(Scenario scenario, int repoNumber, int requestNumber) {
        MappingBuilder builder = get(urlPathEqualTo(
                "/repos/" + scenario.name + "/test-repo-" + repoNumber + "/contents/"));
        addBasicAuthIfNeeded(scenario, builder);
        if (scenario.eTag && scenario.repo2NotModified && repoNumber == 2) {
            builder.withHeader("If-None-Match", equalTo(createRequestETag(requestNumber)));
        }
        return builder;
    }

    private ResponseDefinitionBuilder createRepoRootContentsResponse(Scenario scenario, int repoNumber, int requestNumber) {
        ResponseDefinitionBuilder builder = aResponse();
        if (isRepo3AndRepo3HasNoContent(scenario, repoNumber)) {
            builder.withStatus(404)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("Not Found");
        } else {
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
        }
        return builder;
    }

    private void addBasicAuthIfNeeded(Scenario scenario, MappingBuilder builder) {
        if (nonNull(scenario.accessToken)) {
            builder.withBasicAuth(scenario.accessToken.getUsername(), scenario.accessToken.getValue());
        }
    }

    private String createRequestETag(int requestNumber) {
        return "test-etag-" + requestNumber;
    }

    private String createResponseETag(Scenario scenario, int requestNumber) {
        if (shouldResponseBeModified(scenario, requestNumber)) {
            return "test-modified-etag-" + requestNumber;
        }
        return "test-etag-" + requestNumber;
    }

    private boolean shouldResponseBeModified(Scenario scenario, int requestNumber) {
        return !((requestNumber == 1 && scenario.repoListNotModified) || (requestNumber == 3 && scenario.repo2NotModified));
    }

    private String getRepoContentsFileName(int repoNumber) {
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

    private void createRateLimitResponseHeaders(ResponseDefinitionBuilder builder, int requestNumber) {
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

    private String replaceVars(String value, String baseUrl, String username) {
        return value.replaceAll("\\{\\{baseUrl}}", baseUrl)
                .replaceAll("\\{\\{username}}", username);
    }

    private String readTestFile(String name) {
        return TestFileHelper.readTestFile(name, GitHubApiWireMockFactory.class);
    }

    private WireMockServer create(Consumer<WireMockServer> initializer) {
        WireMockServer wireMockServer = new WireMockServer(PORT);
        initializer.accept(wireMockServer);
        wireMockServer.start();
        return wireMockServer;
    }

    public enum ReposResourceType {
        AUTHENTICATED_USER,
        USER,
        ORGANIZATION
    }

    @Value
    @Builder
    public static class Scenario {

        public static final List<Scenario> ALL_SCENARIOS = new ArrayList<>();
        public static final Scenario ACCESS_TOKEN = accessTokenScenario("personal-access-token", scenarioBuilder -> {});
        public static final Scenario RATE_LIMIT_RESPONSE_HEADERS = accessTokenScenario("rate-limit-response-headers", scenarioBuilder -> scenarioBuilder.rateLimitResponseHeaders(true));
        public static final Scenario ETAG_USER_REPOS_NOT_MODIFIED = accessTokenScenario("etag-user-repos-not-modified", scenarioBuilder -> scenarioBuilder.eTag(true).repoListNotModified(true));
        public static final Scenario ETAG_REPO_2_NOT_MODIFIED = accessTokenScenario("etag-repo-2-not-modified", scenarioBuilder -> scenarioBuilder.eTag(true).repo2NotModified(true));
        public static final Scenario REPO_3_NO_CONTENT = accessTokenScenario("repo-3-no-content", scenarioBuilder -> scenarioBuilder.repo3NoContent(true));
        public static final Scenario INTERNAL_SERVER_ERROR = accessTokenScenario("internal-server-error", scenarioBuilder -> scenarioBuilder.internalServerError(true));
        public static final Scenario REPO_LIST_NOT_FOUND = accessTokenScenario("repo-list-not-found", scenarioBuilder -> scenarioBuilder.repoListNotFound(true));
        public static final Scenario USER = scenario(ReposResourceType.USER, "user", false);
        public static final Scenario USER_WITH_ACCESS_TOKEN = scenario(ReposResourceType.USER, "user-with-personal-access-token", true);
        public static final Scenario ORGANIZATION = scenario(ReposResourceType.ORGANIZATION, "organization", false);
        public static final Scenario ORGANIZATION_WITH_ACCESS_TOKEN = scenario(ReposResourceType.ORGANIZATION, "organization-with-personal-access-token", true);

        ReposResourceType reposResourceType;
        String name;
        GitHubRepoFinderAccessTokenConfig accessToken;
        boolean internalServerError;
        boolean rateLimitResponseHeaders;
        boolean eTag;
        boolean repoListNotModified;
        boolean repo2NotModified;
        boolean repo3NoContent;
        boolean repoListNotFound;

        private static Scenario accessTokenScenario(String name, Consumer<ScenarioBuilder> builderConsumer) {
            ScenarioBuilder builder = new ScenarioBuilder()
                    .reposResourceType(ReposResourceType.AUTHENTICATED_USER)
                    .name(name)
                    .accessToken(createAccessToken(name));
            builderConsumer.accept(builder);
            Scenario scenario = builder.build();
            ALL_SCENARIOS.add(scenario);
            return scenario;
        }

        private static Scenario scenario(ReposResourceType reposResourceType, String name, boolean hasAccessToken) {
            Scenario scenario = new ScenarioBuilder()
                    .reposResourceType(reposResourceType)
                    .name(name)
                    .accessToken(hasAccessToken ? createAccessToken(name) : null)
                    .build();
            ALL_SCENARIOS.add(scenario);
            return scenario;
        }

        private static GitHubRepoFinderAccessTokenConfig createAccessToken(String name) {
            return new GitHubRepoFinderAccessTokenConfig(name + "-auth-username", name + "-personal-access-token");
        }

        public String getBasicAuthUsername() {
            return nonNull(accessToken) ? accessToken.getUsername() : "anonymous";
        }
    }
    
    private static class RequestNumber {

        private int number;

        public int getNext() {
            number++;
            return number;
        }
    }
}
