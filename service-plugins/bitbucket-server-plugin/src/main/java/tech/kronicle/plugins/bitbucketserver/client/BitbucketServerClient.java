package tech.kronicle.plugins.bitbucketserver.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.pluginapi.finders.models.ApiRepo;
import tech.kronicle.plugins.bitbucketserver.config.BitbucketServerConfig;
import tech.kronicle.plugins.bitbucketserver.config.BitbucketServerHostConfig;
import tech.kronicle.plugins.bitbucketserver.constants.BitbucketServerApiPaths;
import tech.kronicle.plugins.bitbucketserver.models.api.BrowseResponse;
import tech.kronicle.plugins.bitbucketserver.models.api.Link;
import tech.kronicle.plugins.bitbucketserver.models.api.PageResponse;
import tech.kronicle.plugins.bitbucketserver.models.api.Repo;
import tech.kronicle.pluginutils.HttpStatuses;
import tech.kronicle.pluginutils.UriVariablesBuilder;

import javax.inject.Inject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static tech.kronicle.pluginutils.BasicAuthUtils.basicAuth;
import static tech.kronicle.pluginutils.HttpClientFactory.createHttpRequestBuilder;
import static tech.kronicle.pluginutils.UriTemplateUtils.expandUriTemplate;

/**
 * See https://docs.atlassian.com/bitbucket-server/rest/7.11.1/bitbucket-rest.html
 * for a description of the API endpoints for Bitbucket Server.  Bitbucket Service
 * is a distinct product from Bitbucket Cloud.
 */
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class BitbucketServerClient {

    private static final List<Integer> GET_REPOS_EXPECTED_STATUS_CODES = List.of(HttpStatuses.OK);
    private static final List<Integer> BROWSE_EXPECTED_STATUS_CODES = List.of(HttpStatuses.OK, HttpStatuses.NOT_FOUND);
    private static final Comparator<RepoAndApiRepo> REPO_AND_API_REPO_COMPARATOR = Comparator.comparing(repoAndApiRepo -> repoAndApiRepo.getApiRepo().getUrl());

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final BitbucketServerConfig config;

    public List<ApiRepo> getNormalRepos() {
        if (isNull(config.getHosts())) {
            return List.of();
        }

        return config.getHosts().stream()
                .map(this::getNormalRepos)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<ApiRepo> getNormalRepos(BitbucketServerHostConfig host) {
        List<Repo> normalRepos = new ArrayList<>();
        Optional<Integer> start = Optional.empty();

        while (true) {
            PageResponse<Repo> page = getReposPage(host, start);

            normalRepos.addAll(getNormalReposFromPage(page));

            if (page.getIsLastPage()) {
                break;
            }

            start = Optional.ofNullable(page.getNextPageStart());
        }

        return normalRepos.stream()
                .map(this::mapRepoToApiRepo)
                .sorted(REPO_AND_API_REPO_COMPARATOR)
                .map(addHasComponentMetadataFileToApiRepo(host))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private PageResponse<Repo> getReposPage(BitbucketServerHostConfig host, Optional<Integer> start) {
        StringBuilder uriBuilder = new StringBuilder()
                .append(host.getBaseUrl())
                .append(BitbucketServerApiPaths.REPOS);
        start.ifPresent(startValue -> uriBuilder.append("?start=")
                .append(startValue));
        String uri = uriBuilder.toString();
        logWebCall(uri);
        HttpRequest request = createHttpRequestBuilder(config.getTimeout())
                .uri(URI.create(uri))
                .header("Authorization", basicAuth(host.getUsername(), host.getPassword()))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        checkResponseStatus(response, GET_REPOS_EXPECTED_STATUS_CODES, uri);
        return objectMapper.readValue(response.body(), new TypeReference<>() { });
    }

    private List<Repo> getNormalReposFromPage(PageResponse<Repo> page) {
        return page.getValues().stream()
                .filter(this::isNormalRepo)
                .collect(Collectors.toList());
    }

    private boolean isNormalRepo(Repo repo) {
        return repo.getScmId().equals("git")
                && repo.getState().equals("AVAILABLE")
                && repo.getProject().getType().equals("NORMAL");
    }

    private RepoAndApiRepo mapRepoToApiRepo(Repo repo) {
        return new RepoAndApiRepo(repo, new ApiRepo(getHttpCloneLink(repo).getHref(), null));
    }

    private Link getHttpCloneLink(Repo repo) {
        return repo.getLinks().getClone().stream()
                .filter(this::isHttpLink)
                .findFirst().get();
    }

    private boolean isHttpLink(Link link) {
        return link.getName().equals("http");
    }

    private Function<RepoAndApiRepo, ApiRepo> addHasComponentMetadataFileToApiRepo(BitbucketServerHostConfig host) {
        return repoAndApiRepo -> repoAndApiRepo.getApiRepo().withHasComponentMetadataFile(hasComponentMetadataFile(host, repoAndApiRepo.getRepo()));
    }

    @SneakyThrows
    private boolean hasComponentMetadataFile(BitbucketServerHostConfig host, Repo repo) {
        String uriTemplate = host.getBaseUrl() + BitbucketServerApiPaths.BROWSE + "/component-metadata.yaml?type=true";
        Map<String, String> uriVariables = UriVariablesBuilder.builder()
                .addUriVariable("projectKey", repo.getProject().getKey())
                .addUriVariable("repositorySlug", repo.getSlug())
                .build();
        String uri = expandUriTemplate(uriTemplate, uriVariables);

        logWebCall(uri);

        HttpRequest request = createHttpRequestBuilder(config.getTimeout())
                .uri(URI.create(uri))
                .header("Authorization", basicAuth(host.getUsername(), host.getPassword()))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        checkResponseStatus(response, BROWSE_EXPECTED_STATUS_CODES, uri);
        BrowseResponse browseResponse = objectMapper.readValue(response.body(), BrowseResponse.class);
        return Optional.ofNullable(browseResponse)
                .map(BrowseResponse::getType)
                .map(type -> type.equals("FILE"))
                .orElse(false);
    }

    private void checkResponseStatus(HttpResponse<String> response, List<Integer> expectedStatusCodes, String uri) {
        if (!expectedStatusCodes.contains(response.statusCode())) {
            BitbucketServerClientException exception = new BitbucketServerClientException(
                    uri,
                    response.statusCode(),
                    response.body()
            );
            log.warn(exception.getMessage());
            throw exception;
        }
    }

    private void logWebCall(String uri) {
        if (log.isInfoEnabled()) {
            log.info("Calling {}", uri);
        }
    }

    @Value
    private static class RepoAndApiRepo {

        Repo repo;
        ApiRepo apiRepo;
    }
}
