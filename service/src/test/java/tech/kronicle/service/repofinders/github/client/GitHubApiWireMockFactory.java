package tech.kronicle.service.repofinders.github.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import lombok.Getter;
import lombok.ToString;
import tech.kronicle.service.repofinders.github.config.GitHubRepoFinderPersonalAccessTokenConfig;
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

    public static WireMockServer create() {
        return create(wireMockServer -> {
            String baseUrl = "http://localhost:" + PORT;
            Scenario.ALL_SCENARIOS.forEach(scenario -> stubScenarioSpecificResponses(wireMockServer, baseUrl, scenario));
        });
    }

    private static void stubScenarioSpecificResponses(WireMockServer wireMockServer, String baseUrl, Scenario scenario) {
        RequestNumber requestNumber = new RequestNumber();
        int reposRequestNumber = requestNumber.getNext();
        MappingBuilder request = createReposRequest(scenario, reposRequestNumber);
        if (scenario.internalServerError) {
            wireMockServer.stubFor(request
                    .willReturn(aResponse()
                            .withStatus(500)
                            .withHeader("Content-Type", "text/plain")
                            .withBody("Internal Server Error")));
        } else {
            wireMockServer.stubFor(request
                    .willReturn(createReposResponse(baseUrl, scenario, reposRequestNumber)));
            IntStream.range(1, 5).forEach(repoNumber -> {
                int repoRootContentsRequestNumber = requestNumber.getNext();
                wireMockServer.stubFor(createRepoRootContentsRequest(scenario, repoNumber, repoRootContentsRequestNumber)
                        .willReturn(createRepoRootContentsResponse(scenario, repoNumber, repoRootContentsRequestNumber)));
            });
        }
    }

    private static boolean isRepo3AndRepo3HasNoContent(Scenario scenario, int repoNumber) {
        return repoNumber == 3 && scenario.repo3NoContent;
    }

    private static MappingBuilder createReposRequest(Scenario scenario, int requestNumber) {
        MappingBuilder builder = get(urlPathEqualTo(getReposUrl(scenario)));
        addBasicAuthIfNeeded(scenario, builder);
        if (scenario.eTag && scenario.userReposNotModified) {
            builder.withHeader("If-None-Match", equalTo(createRequestETag(requestNumber)));
        }
        return builder;
    }

    private static String getReposUrl(Scenario scenario) {
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

    private static ResponseDefinitionBuilder createReposResponse(String baseUrl, Scenario scenario, int requestNumber) {
        ResponseDefinitionBuilder builder = aResponse();
        if (scenario.eTag && scenario.userReposNotModified) {
            builder.withStatus(304);
        } else {
            builder.withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(replaceVars(readTestFile("github-api-responses/user-repos.json"), baseUrl, scenario.name));
        }
        builder.withHeader("ETag", createResponseETag(scenario, requestNumber));
        if (scenario.rateLimitResponseHeaders) {
            createRateLimitResponseHeaders(builder, requestNumber);
        }
        return builder;
    }

    private static MappingBuilder createRepoRootContentsRequest(Scenario scenario, int repoNumber, int requestNumber) {
        MappingBuilder builder = get(urlPathEqualTo(
                "/repos/" + scenario.name + "/test-repo-" + repoNumber + "/contents/"));
        addBasicAuthIfNeeded(scenario, builder);
        if (scenario.eTag && scenario.repo2NotModified && repoNumber == 2) {
            builder.withHeader("If-None-Match", equalTo(createRequestETag(requestNumber)));
        }
        return builder;
    }

    private static ResponseDefinitionBuilder createRepoRootContentsResponse(Scenario scenario, int repoNumber, int requestNumber) {
        ResponseDefinitionBuilder builder = aResponse();
        if (isRepo3AndRepo3HasNoContent(scenario, repoNumber)) {
            builder.withStatus(404);
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

    private static void addBasicAuthIfNeeded(Scenario scenario, MappingBuilder builder) {
        if (nonNull(scenario.personalAccessToken)) {
            builder.withBasicAuth(scenario.personalAccessToken.getUsername(), scenario.personalAccessToken.getPersonalAccessToken());
        }
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

    public enum ReposResourceType {
        AUTHENTICATED_USER,
        USER,
        ORGANIZATION
    }

    @Getter
    @ToString
    public static final class Scenario {

        public static final List<Scenario> ALL_SCENARIOS = new ArrayList<>();
        public static final Scenario PERSONAL_ACCESS_TOKEN = personalAccessTokenScenario("personal-access-token", false, false, false, false, false, false);
        public static final Scenario RATE_LIMIT_RESPONSE_HEADERS = personalAccessTokenScenario("rate-limit-response-headers", false, true, false, false, false, false);
        public static final Scenario ETAG_USER_REPOS_NOT_MODIFIED = personalAccessTokenScenario("etag-user-repos-not-modified", false, false, true, true, false, false);
        public static final Scenario ETAG_REPO_2_NOT_MODIFIED = personalAccessTokenScenario("etag-repo-2-not-modified", false, false, true, false, true, false);
        public static final Scenario REPO_2_NO_CONTENT = personalAccessTokenScenario("repo-2-no-content", false, false, false, false, false, true);
        public static final Scenario INTERNAL_SERVER_ERROR = personalAccessTokenScenario("internal-server-error", true, false, false, false, false, false);
        public static final Scenario USER = scenario(ReposResourceType.USER, "user", false);
        public static final Scenario USER_WITH_PERSONAL_ACCESS_TOKEN = scenario(ReposResourceType.USER, "user-with-personal-access-token", true);
        public static final Scenario ORGANIZATION = scenario(ReposResourceType.ORGANIZATION, "organization", false);
        public static final Scenario ORGANIZATION_WITH_PERSONAL_ACCESS_TOKEN = scenario(ReposResourceType.ORGANIZATION, "organization-with-personal-access-token", true);

        private final ReposResourceType reposResourceType;
        private final String name;
        private final GitHubRepoFinderPersonalAccessTokenConfig personalAccessToken;
        private final Boolean internalServerError;
        private final Boolean rateLimitResponseHeaders;
        private final Boolean eTag;
        private final Boolean userReposNotModified;
        private final Boolean repo2NotModified;
        private final Boolean repo3NoContent;

        private static Scenario personalAccessTokenScenario(String name, Boolean internalServerError, Boolean rateLimitResponseHeaders, Boolean eTag, Boolean userReposNotModified, Boolean repo2NotModified, Boolean repo3NoContent) {
            Scenario scenario = new Scenario(
                    ReposResourceType.AUTHENTICATED_USER,
                    name,
                    createPersonalAccessToken(name),
                    internalServerError,
                    rateLimitResponseHeaders,
                    eTag,
                    userReposNotModified,
                    repo2NotModified,
                    repo3NoContent);
            ALL_SCENARIOS.add(scenario);
            return scenario;
        }

        private static Scenario scenario(ReposResourceType reposResourceType, String name, boolean hasPersonalAccessToken) {
            Scenario scenario = new Scenario(
                    reposResourceType,
                    name,
                    hasPersonalAccessToken ? createPersonalAccessToken(name) : null,
                    false,
                    false,
                    false,
                    false,
                    false, false);
            ALL_SCENARIOS.add(scenario);
            return scenario;
        }

        private static GitHubRepoFinderPersonalAccessTokenConfig createPersonalAccessToken(String name) {
            return new GitHubRepoFinderPersonalAccessTokenConfig(name + "-auth-username", name + "-personal-access-token");
        }

        private Scenario(ReposResourceType reposResourceType, String name, GitHubRepoFinderPersonalAccessTokenConfig personalAccessToken, Boolean internalServerError, Boolean rateLimitResponseHeaders, Boolean eTag, Boolean userReposNotModified, Boolean repo2NotModified, Boolean repo3NoContent) {
            this.reposResourceType = reposResourceType;
            this.name = name;
            this.personalAccessToken = personalAccessToken;
            this.internalServerError = internalServerError;
            this.rateLimitResponseHeaders = rateLimitResponseHeaders;
            this.eTag = eTag;
            this.userReposNotModified = userReposNotModified;
            this.repo2NotModified = repo2NotModified;
            this.repo3NoContent = repo3NoContent;
        }

        public String getBasicAuthUsername() {
            return nonNull(personalAccessToken) ? personalAccessToken.getUsername() : "anonymous";
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
