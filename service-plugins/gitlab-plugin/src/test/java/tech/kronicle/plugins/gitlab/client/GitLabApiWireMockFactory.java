package tech.kronicle.plugins.gitlab.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import lombok.Getter;
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
                        RepoMetadataScenario repoMetadataScenario = getRepoMetadataScenario(repoNumber.get());

                        switch (repoMetadataScenario) {
                            case KRONICLE_YAML:
                                stubRepoMetadataFileRequest(wireMockServer, scenario, repoNumber.get(), RepoMetadataScenario.KRONICLE_YAML, true);
                                break;
                            case NONE:
                                stubRepoMetadataFileRequest(wireMockServer, scenario, repoNumber.get(), RepoMetadataScenario.KRONICLE_YAML, false);
                                break;
                        }
                    }
                });
            });
        }
    }

    private void stubRepoMetadataFileRequest(WireMockServer wireMockServer, Scenario scenario, int repoNumber, RepoMetadataScenario repoMetadataScenario, boolean hasContent) {
        wireMockServer.stubFor(createRepoRootContentsRequest(scenario, repoNumber, repoMetadataScenario)
                .willReturn(createRepoRootContentsResponse(hasContent)));
    }

    private RepoMetadataScenario getRepoMetadataScenario(int repoNumber) {
        switch (repoNumber) {
            case 1:
            case 2:
                return RepoMetadataScenario.KRONICLE_YAML;
            case 3:
                return RepoMetadataScenario.NO_DEFAULT_BRANCH;
        }

        return RepoMetadataScenario.NONE;
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

    private String createRepoListResponseBody(int pageNumber) {
        ArrayNode responseBody = objectMapper.createArrayNode();
        IntStream.range(1, PAGE_SIZE + 1).forEach(pageItemNumber -> {
            int repoNumber = getRepoNumber(pageNumber, pageItemNumber);
            RepoMetadataScenario repoMetadataScenario = getRepoMetadataScenario(repoNumber);
            if (repoNumber <= REPO_COUNT) {
                ObjectNode repo = objectMapper.createObjectNode()
                        .put("id", repoNumber)
                        .put("http_url_to_repo", "https://example.com/repo-" + repoNumber + "-" + repoMetadataScenario + ".git")
                        .put("unknown", true);
                if (repoMetadataScenario != RepoMetadataScenario.NO_DEFAULT_BRANCH) {
                    repo.put("default_branch", "branch-" + repoNumber);
                }
                responseBody.add(repo);
            }
        });
        try {
            return objectMapper.writeValueAsString(responseBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private int getRepoNumber(int pageNumber, int pageItemNumber) {
        return (pageNumber - 1) * PAGE_SIZE + pageItemNumber;
    }

    private MappingBuilder createRepoRootContentsRequest(
            Scenario scenario,
            int repoNumber,
            RepoMetadataScenario repoMetadataScenario
    ) {
        MappingBuilder builder = get(urlEqualTo(
                "/api/v4/projects/" + repoNumber +
                "/repository/files/" + getMetadataFilename(repoMetadataScenario) +
                "?ref=branch-" + repoNumber));
        addAccessTokenHeaderIfWanted(scenario, builder);
        return builder;
    }

    private String getMetadataFilename(RepoMetadataScenario repoMetadataScenario) {
        if (repoMetadataScenario == RepoMetadataScenario.KRONICLE_YAML) {
            return "kronicle.yaml";
        }
        throw new RuntimeException("Unexpected repo metadata scenario");
    }

    private ResponseDefinitionBuilder createRepoRootContentsResponse(boolean hasContent) {
        if (hasContent) {
            return aResponse().withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{}");
        } else {
            return aResponse().withStatus(404)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("Not Found");
        }
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

    public enum RepoMetadataScenario {
        NONE,
        KRONICLE_YAML,
        NO_DEFAULT_BRANCH
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