package tech.kronicle.plugins.gitlab.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import tech.kronicle.plugins.gitlab.config.GitLabAccessTokenConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.google.common.base.Strings.padStart;
import static java.util.Objects.nonNull;

public class GitLabApiWireMockFactory {

    public static final int PORT = 36209;
    private static final int PAGE_COUNT = 2;
    public static final int PAGE_SIZE = 5;
    private static final int REPO_COUNT = 8;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public WireMockServer create(Scenario scenario) {
        return create(wireMockServer -> stubScenarioSpecificResponses(wireMockServer, scenario));
    }

    private void stubScenarioSpecificResponses(WireMockServer wireMockServer, Scenario scenario) {
        if (scenario.reposResourceType == ReposResourceType.INTERNAL_SERVER_ERROR) {
            MappingBuilder request = createReposRequest(scenario, 1);
            wireMockServer.stubFor(request
                    .willReturn(aResponse()
                            .withStatus(500)
                            .withHeader("Content-Type", "text/plain")
                            .withBody("Internal Server Error")));
        } else {
            AtomicInteger repoNumber = new AtomicInteger();
            IntStream.range(1, PAGE_COUNT + 1).forEach(pageNumber -> {
                MappingBuilder request = createReposRequest(scenario, pageNumber);
                wireMockServer.stubFor(request.willReturn(createRepoListResponse(pageNumber)));

                IntStream.range(1, PAGE_SIZE + 1).forEach(pageItemNumber -> {
                    repoNumber.incrementAndGet();

                    if (repoNumber.get() <= REPO_COUNT) {
                        RepoScenario repoScenario = getRepoScenario(repoNumber.get());
                        stubRepoMetadataFileRequest(wireMockServer, scenario, repoNumber.get(), repoScenario);
                        stubRepoPipelinesRequest(wireMockServer, scenario, repoNumber.get(), repoScenario);
                        stubRepoPipelineJobsRequest(wireMockServer, scenario, repoNumber.get());
                    }
                });
            });
        }
    }
    
    private void stubRepoMetadataFileRequest(
            WireMockServer wireMockServer,
            Scenario scenario,
            int repoNumber,
            RepoScenario repoScenario
    ) {
        wireMockServer.stubFor(createRepoRootContentsRequest(scenario, repoNumber)
                .willReturn(createRepoRootContentsResponse(repoScenario)));
    }

    private RepoScenario getRepoScenario(int repoNumber) {
        switch (repoNumber) {
            case 3:
                return RepoScenario.NO_DEFAULT_BRANCH;
            case 5:
                return RepoScenario.NO_KRONICLE_METADATA_FILE;
            case 7:
                return RepoScenario.PIPELINES_FORBIDDEN;
            default:
                return RepoScenario.NORMAL;
        }
    }

    private MappingBuilder createReposRequest(Scenario scenario, int pageNumber) {
        MappingBuilder builder = get(urlPathEqualTo(getReposUrl(scenario)));
        builder.withQueryParam("page", equalTo(Integer.toString(pageNumber)))
                .withQueryParam("per_page", equalTo(Integer.toString(PAGE_SIZE)));
        addAccessTokenHeaderIfWanted(scenario, builder);
        return builder;
    }

    private String getReposUrl(Scenario scenario) {
        switch (scenario.reposResourceType) {
            case ALL:
            case INTERNAL_SERVER_ERROR:
                return "/api/v4/projects";
            case USER:
                return "/api/v4/users/example-username/projects";
            case GROUP:
                return "/api/v4/groups/example-group-path/projects";
           default:
                throw new RuntimeException("Unexpected repos resource type " + scenario.reposResourceType);
        }
    }

    private ResponseDefinitionBuilder createRepoListResponse(int pageNumber) {
        ResponseDefinitionBuilder builder = aResponse();
        builder.withStatus(200)
                .withHeader("Content-Type", "application/json");
        builder.withHeader(
                "X-Next-Page",
                (pageNumber < PAGE_COUNT) ? Integer.toString(pageNumber + 1) : ""
        );
        builder.withBody(createRepoListResponseBody(pageNumber));
        return builder;
    }

    @SneakyThrows
    private String createRepoListResponseBody(int pageNumber) {
        ArrayNode responseBody = objectMapper.createArrayNode();
        IntStream.range(1, PAGE_SIZE + 1).forEach(pageItemNumber -> {
            int repoNumber = getRepoNumber(pageNumber, pageItemNumber);
            if (repoNumber <= REPO_COUNT) {
                RepoScenario repoScenario = getRepoScenario(repoNumber);
                ObjectNode repo = objectMapper.createObjectNode()
                        .put("id", repoNumber)
                        .put("http_url_to_repo", "https://example.com/repo-" + repoNumber + "-" + repoScenario + ".git")
                        .put("unknown", true);
                if (repoScenario != RepoScenario.NO_DEFAULT_BRANCH) {
                    repo.put("default_branch", "branch-" + repoNumber);
                }
                responseBody.add(repo);
            }
        });
        return objectMapper.writeValueAsString(responseBody);
    }

    private int getRepoNumber(int pageNumber, int pageItemNumber) {
        return (pageNumber - 1) * PAGE_SIZE + pageItemNumber;
    }

    private MappingBuilder createRepoRootContentsRequest(
            Scenario scenario,
            int repoNumber
    ) {
        MappingBuilder builder = get(urlEqualTo(
                "/api/v4/projects/" + repoNumber +
                "/repository/files/kronicle.yaml" +
                "?ref=branch-" + repoNumber));
        addAccessTokenHeaderIfWanted(scenario, builder);
        return builder;
    }

    private ResponseDefinitionBuilder createRepoRootContentsResponse(RepoScenario repoScenario) {
        if (repoScenario == RepoScenario.NO_KRONICLE_METADATA_FILE) {
            return aResponse().withStatus(404)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("Not Found");
        } else {
            return aResponse().withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{}");
        }
    }

    private void stubRepoPipelinesRequest(
            WireMockServer wireMockServer,
            Scenario scenario,
            int repoNumber,
            RepoScenario repoScenario
    ) {
        wireMockServer.stubFor(createRepoPipelinesRequest(scenario, repoNumber)
                .willReturn(createRepoPipelinesResponse(repoNumber, repoScenario)));
    }

    private MappingBuilder createRepoPipelinesRequest(
            Scenario scenario,
            int repoNumber
    ) {
        MappingBuilder builder = get(urlEqualTo(
                "/api/v4/projects/" + repoNumber +
                        "/pipelines?ref=" + createDefaultBranch(repoNumber) +
                        "&page=1&per_page=5"
        ));
        addAccessTokenHeaderIfWanted(scenario, builder);
        return builder;
    }

    private String createDefaultBranch(int repoNumber) {
        return "branch-" + repoNumber;
    }

    @SneakyThrows
    private ResponseDefinitionBuilder createRepoPipelinesResponse(
            int repoNumber,
            RepoScenario repoScenario
    ) {
        if (repoScenario == RepoScenario.PIPELINES_FORBIDDEN) {
            return aResponse()
                    .withStatus(403)
                    .withHeader("Content-Type", "application/json")
                    .withBody(createRepoPipelinesNotAuthorisedResponseBody());
        } else {
            return aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withHeader("X-Next-Page", "")
                    .withBody(createRepoPipelinesResponseBody(repoNumber));
        }
    }

    @SneakyThrows
    private String createRepoPipelinesResponseBody(int repoNumber) {
        ArrayNode responseBody = objectMapper.createArrayNode();
        IntStream.range(1, PAGE_SIZE + 1).forEach(pageItemNumber -> {
            ObjectNode pipeline = objectMapper.createObjectNode()
                    .put("id", 1000 + pageItemNumber)
                    .put("iid", pageItemNumber)
                    .put("project_id", repoNumber)
                    .put("sha", "test-sha-" + repoNumber + "-" + pageItemNumber)
                    .put("ref", createDefaultBranch(repoNumber))
                    .put("status", "success")
                    .put("source", "push")
                    .put("created_at", createTimestamp(repoNumber, pageItemNumber, 1))
                    .put("updated_at", createTimestamp(repoNumber, pageItemNumber, 2))
                    .put("web_url", "https://example.com/web-url-" + repoNumber + "-" + pageItemNumber);
            responseBody.add(pipeline);
        });
        return objectMapper.writeValueAsString(responseBody);
    }

    @SneakyThrows
    private String createRepoPipelinesNotAuthorisedResponseBody() {
        return objectMapper.writeValueAsString(
                objectMapper.createObjectNode()
                        .put("message", "403 Forbidden")
        );
    }

    private String createTimestamp(int repoNumber, int pageItemNumber, int timestampNumber) {
        return (2000 + repoNumber)
                + "-"
                + padStart(Integer.toString(pageItemNumber), 2, '0')
                + "-"
                + padStart(Integer.toString(timestampNumber), 2, '0')
                + "T00:00:00.000Z";
    }

    private void stubRepoPipelineJobsRequest(WireMockServer wireMockServer, Scenario scenario, int repoNumber) {
        wireMockServer.stubFor(createRepoPipelineJobsRequest(scenario, repoNumber)
                .willReturn(createRepoPipelineJobsResponse(repoNumber)));
    }

    private MappingBuilder createRepoPipelineJobsRequest(
            Scenario scenario,
            int repoNumber
    ) {
        MappingBuilder builder = get(urlEqualTo(
                "/api/v4/projects/" + repoNumber + "/pipelines/1001/jobs?page=1&per_page=5"
        ));
        addAccessTokenHeaderIfWanted(scenario, builder);
        return builder;
    }

    @SneakyThrows
    private ResponseDefinitionBuilder createRepoPipelineJobsResponse(int repoNumber) {
        return aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withHeader("X-Next-Page", "")
                .withBody(createRepoPipelineJobsResponseBody(repoNumber));
    }

    @SneakyThrows
    private String createRepoPipelineJobsResponseBody(int repoNumber) {
        ArrayNode responseBody = objectMapper.createArrayNode();
        IntStream.range(1, PAGE_SIZE + 1).forEach(pageItemNumber -> {
            ObjectNode job = objectMapper.createObjectNode()
                    .put("name", "Test name " + repoNumber + " " + pageItemNumber)
                    .put("status", "success")
                    .put("web_url", "https://example.com/web-url-" + repoNumber + "-" + pageItemNumber)
                    .put("created_at", createTimestamp(repoNumber, pageItemNumber, 1))
                    .put("started_at", createTimestamp(repoNumber, pageItemNumber, 2))
                    .put("finished_at", createTimestamp(repoNumber, pageItemNumber, 3));
            job.putObject("user")
                    .put("avatar_url", "https://example.com/avatar-url-" + repoNumber + "-" + pageItemNumber);
            responseBody.add(job);
        });
        return objectMapper.writeValueAsString(responseBody);
    }

    private void addAccessTokenHeaderIfWanted(Scenario scenario, MappingBuilder builder) {
        if (nonNull(scenario.getAccessToken())) {
            builder.withHeader("PRIVATE-TOKEN", equalTo(scenario.accessToken.getValue()));
        }
    }

    private WireMockServer create(Consumer<WireMockServer> initializer) {
        WireMockServer wireMockServer = new WireMockServer(PORT);
        initializer.accept(wireMockServer);
        wireMockServer.start();
        return wireMockServer;
    }

    public enum ReposResourceType {
        INTERNAL_SERVER_ERROR,
        ALL,
        USER,
        GROUP
    }

    @Getter
    public static class Scenario {

        public static final List<Scenario> ALL_SCENARIOS = new ArrayList<>();
        public static final Scenario INTERNAL_SERVER_ERROR = new Scenario(ReposResourceType.INTERNAL_SERVER_ERROR, true);
        public static final Scenario ALL_WITH_ACCESS_TOKEN = new Scenario(ReposResourceType.ALL, true);
        public static final Scenario USER_WITH_ACCESS_TOKEN = new Scenario(ReposResourceType.USER, true);
        public static final Scenario GROUP_WITH_ACCESS_TOKEN = new Scenario(ReposResourceType.GROUP, true);
        public static final Scenario ALL_WITHOUT_ACCESS_TOKEN = new Scenario(ReposResourceType.ALL, false);
        public static final Scenario USER_WITHOUT_ACCESS_TOKEN = new Scenario(ReposResourceType.USER, false);
        public static final Scenario GROUP_WITHOUT_ACCESS_TOKEN = new Scenario(ReposResourceType.GROUP, false);

        ReposResourceType reposResourceType;
        GitLabAccessTokenConfig accessToken;

        private Scenario(ReposResourceType reposResourceType, boolean hasAccessToken) {
            this.reposResourceType = reposResourceType;
            if (hasAccessToken) {
                accessToken = new GitLabAccessTokenConfig("access-token-" + reposResourceType.name());
            } else {
                accessToken = null;
            }
            ALL_SCENARIOS.add(this);
        }
    }
}
