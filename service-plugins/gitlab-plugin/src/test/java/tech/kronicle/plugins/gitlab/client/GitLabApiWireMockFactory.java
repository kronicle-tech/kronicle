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
import tech.kronicle.plugins.gitlab.testutils.RepoScenario;

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
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.nonNull;

public class GitLabApiWireMockFactory {

    public static final int PORT = 36209;
    private static final int PAGE_COUNT = 2;
    public static final int PAGE_SIZE = 5;
    private static final int REPO_COUNT = 8;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public WireMockServer createRepoRequests(ReposScenario scenario) {
        return create(wireMockServer -> stubRepoRequests(wireMockServer, scenario));
    }

    public WireMockServer createJobRequests(JobsScenario scenario) {
        return create(wireMockServer -> stubJobRequests(wireMockServer, scenario));
    }

    private void stubRepoRequests(WireMockServer wireMockServer, ReposScenario scenario) {
        if (scenario.type == ReposScenarioType.INTERNAL_SERVER_ERROR) {
            wireMockServer.stubFor(createReposRequest(scenario, 1)
                    .willReturn(createInternalServerErrorResponse()));
        } else {
            AtomicInteger repoNumber = new AtomicInteger();
            IntStream.range(1, PAGE_COUNT + 1).forEach(pageNumber -> {
                wireMockServer.stubFor(
                        createReposRequest(scenario, pageNumber).willReturn(createRepoListResponse(pageNumber))
                );

                IntStream.range(1, PAGE_SIZE + 1).forEach(pageItemNumber -> {
                    repoNumber.incrementAndGet();

                    if (repoNumber.get() <= REPO_COUNT) {
                        RepoScenario repoScenario = getRepoScenario(repoNumber.get());
                        stubRepoMetadataFileRequest(wireMockServer, scenario, repoNumber.get(), repoScenario);
                    }
                });
            });
        }
    }

    private void stubJobRequests(WireMockServer wireMockServer, JobsScenario scenario) {
        int repoNumber = 1;
        stubRepoPipelinesRequest(wireMockServer, scenario, repoNumber);
        stubRepoPipelineJobsRequest(wireMockServer, scenario, repoNumber);
    }

    private void stubRepoMetadataFileRequest(
            WireMockServer wireMockServer,
            ReposScenario scenario,
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

    private MappingBuilder createReposRequest(ReposScenario scenario, int pageNumber) {
        MappingBuilder builder = get(urlPathEqualTo(getReposUrl(scenario)));
        builder.withQueryParam("page", equalTo(Integer.toString(pageNumber)))
                .withQueryParam("per_page", equalTo(Integer.toString(PAGE_SIZE)));
        addAccessTokenHeaderIfWanted(scenario, builder);
        return builder;
    }

    private String getReposUrl(ReposScenario scenario) {
        switch (scenario.type) {
            case ALL:
            case INTERNAL_SERVER_ERROR:
                return "/api/v4/projects";
            case USER:
                return "/api/v4/users/example-username/projects";
            case GROUP:
                return "/api/v4/groups/example-group-path/projects";
           default:
                throw new RuntimeException("Unexpected repos resource type " + scenario.type);
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
            ReposScenario scenario,
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
            JobsScenario scenario,
            int repoNumber
    ) {
        wireMockServer.stubFor(createRepoPipelinesRequest(scenario, repoNumber)
                .willReturn(createRepoPipelinesResponse(scenario, repoNumber)));
    }

    private MappingBuilder createRepoPipelinesRequest(
            JobsScenario scenario,
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
            JobsScenario scenario,
            int repoNumber
    ) {
        switch (scenario.type) {
            case INTERNAL_SERVER_ERROR:
                return createInternalServerErrorResponse();
            case PIPELINES_FORBIDDEN:
                return aResponse()
                        .withStatus(403)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createRepoPipelinesNotAuthorisedResponseBody());
            default:
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

    private void stubRepoPipelineJobsRequest(WireMockServer wireMockServer, JobsScenario scenario, int repoNumber) {
        wireMockServer.stubFor(createRepoPipelineJobsRequest(scenario, repoNumber)
                .willReturn(createRepoPipelineJobsResponse(repoNumber)));
    }

    private MappingBuilder createRepoPipelineJobsRequest(
            JobsScenario scenario,
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
                    .put("name", "Test Job Name " + repoNumber + " " + pageItemNumber)
                    .put("status", "test-job-status-" + repoNumber + "-" + pageItemNumber)
                    .put("web_url", "https://example.com/test-job-web-url-" + repoNumber + "-" + pageItemNumber)
                    .put("created_at", createTimestamp(repoNumber, pageItemNumber, 1))
                    .put("started_at", createTimestamp(repoNumber, pageItemNumber, 2))
                    .put("finished_at", createTimestamp(repoNumber, pageItemNumber, 3));
            job.putObject("user")
                    .put("avatar_url", "https://example.com/test-user-avatar-" + repoNumber + "-" + pageItemNumber);
            responseBody.add(job);
        });
        return objectMapper.writeValueAsString(responseBody);
    }

    private void addAccessTokenHeaderIfWanted(Scenario scenario, MappingBuilder builder) {
        if (nonNull(scenario.getAccessToken())) {
            builder.withHeader("PRIVATE-TOKEN", equalTo(scenario.getAccessToken().getValue()));
        }
    }

    private WireMockServer create(Consumer<WireMockServer> initializer) {
        WireMockServer wireMockServer = new WireMockServer(PORT);
        initializer.accept(wireMockServer);
        wireMockServer.start();
        return wireMockServer;
    }

    private ResponseDefinitionBuilder createInternalServerErrorResponse() {
        return aResponse()
                .withStatus(500)
                .withHeader("Content-Type", "text/plain")
                .withBody("Internal Server Error");
    }

    public enum ReposScenarioType {
        INTERNAL_SERVER_ERROR,
        ALL,
        USER,
        GROUP
    }

    public enum JobsScenarioType {
        INTERNAL_SERVER_ERROR,
        NORMAL,
        PIPELINES_FORBIDDEN
    }

    public interface Scenario {

        GitLabAccessTokenConfig getAccessToken();
    }

    @Getter
    public static class ReposScenario implements Scenario {

        private static final List<ReposScenario> MUTABLE_NORMAL_SCENARIOS = new ArrayList<>();
        public static final List<ReposScenario> NORMAL_SCENARIOS = unmodifiableList(MUTABLE_NORMAL_SCENARIOS);

        public static final ReposScenario INTERNAL_SERVER_ERROR = new ReposScenario(true, ReposScenarioType.INTERNAL_SERVER_ERROR);
        public static final ReposScenario ALL_WITH_ACCESS_TOKEN = new ReposScenario(true, ReposScenarioType.ALL);
        public static final ReposScenario USER_WITH_ACCESS_TOKEN = new ReposScenario(true, ReposScenarioType.USER);
        public static final ReposScenario GROUP_WITH_ACCESS_TOKEN = new ReposScenario(true, ReposScenarioType.GROUP);
        public static final ReposScenario ALL_WITHOUT_ACCESS_TOKEN = new ReposScenario(false, ReposScenarioType.ALL);
        public static final ReposScenario USER_WITHOUT_ACCESS_TOKEN = new ReposScenario(false, ReposScenarioType.USER);
        public static final ReposScenario GROUP_WITHOUT_ACCESS_TOKEN = new ReposScenario(false, ReposScenarioType.GROUP);

        ReposScenarioType type;
        GitLabAccessTokenConfig accessToken;

        private ReposScenario(boolean hasAccessToken, ReposScenarioType type) {
            if (hasAccessToken) {
                accessToken = new GitLabAccessTokenConfig("access-token-" + type.name());
            } else {
                accessToken = null;
            }
            this.type = type;
            if (type != ReposScenarioType.INTERNAL_SERVER_ERROR) {
                MUTABLE_NORMAL_SCENARIOS.add(this);
            }
        }
    }

    @Getter
    public static class JobsScenario implements Scenario {

        private static final List<JobsScenario> MUTABLE_NORMAL_SCENARIOS = new ArrayList<>();
        public static final List<JobsScenario> NORMAL_SCENARIOS = unmodifiableList(MUTABLE_NORMAL_SCENARIOS);

        public static final JobsScenario INTERNAL_SERVER_ERROR = new JobsScenario(true, JobsScenarioType.INTERNAL_SERVER_ERROR);
        public static final JobsScenario NORMAL_WITH_ACCESS_TOKEN = new JobsScenario(true, JobsScenarioType.NORMAL);
        public static final JobsScenario PIPELINES_FORBIDDEN_WITH_ACCESS_TOKEN = new JobsScenario(true, JobsScenarioType.PIPELINES_FORBIDDEN);
        public static final JobsScenario NORMAL_WITHOUT_ACCESS_TOKEN = new JobsScenario(false, JobsScenarioType.NORMAL);
        public static final JobsScenario PIPELINES_FORBIDDEN_WITHOUT_ACCESS_TOKEN = new JobsScenario(false, JobsScenarioType.PIPELINES_FORBIDDEN);

        JobsScenarioType type;
        GitLabAccessTokenConfig accessToken;

        private JobsScenario(boolean hasAccessToken, JobsScenarioType type) {
            if (hasAccessToken) {
                accessToken = new GitLabAccessTokenConfig("access-token-" + type.name());
            } else {
                accessToken = null;
            }
            this.type = type;
            if (this.type != JobsScenarioType.INTERNAL_SERVER_ERROR) {
                MUTABLE_NORMAL_SCENARIOS.add(this);
            }
        }
    }
}
