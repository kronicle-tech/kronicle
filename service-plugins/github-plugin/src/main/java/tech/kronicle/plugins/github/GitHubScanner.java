package tech.kronicle.plugins.github;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.scanners.ComponentScanner;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.github.client.GitHubClient;
import tech.kronicle.plugins.github.config.GitHubConfig;
import tech.kronicle.sdk.models.Component;

import javax.inject.Inject;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class GitHubScanner extends ComponentScanner {

    private static Pattern GITHUB_REPO_URL_PATH_PATTERN = Pattern.compile(
            "^/(?<owner>[a-zA-Z0-9]+[-a-zA-Z0-9]+)/(?<repo>[a-zA-Z0-9]+[-a-zA-Z0-9]+).git$"
    );

    private final GitHubConfig config;
    private final GitHubClient client;

    @Override
    public String description() {
        return "Find the status of any GitHub Actions build for a component's repo";
    }

    @SneakyThrows
    @Override
    public Output<Void> scan(Component input) {
        if (isNull(input.getRepo())) {
            return Output.of(UnaryOperator.identity());
        }

        URL repoUrl = new URL(input.getRepo().getUrl());

        if (!Objects.equals(repoUrl.getHost(), config.getDomain())) {
            log.info(
                    "Skipping component as repo URL host \"{}\" does not match \"{}\"",
                    repoUrl.getHost(),
                    config.getDomain()
            );
            return Output.of(UnaryOperator.identity());
        }

        Matcher pathMatcher = GITHUB_REPO_URL_PATH_PATTERN.matcher(repoUrl.getPath());

        if (!pathMatcher.matches()) {
            log.info(
                    "Skipping component as repo URL path \"{}\" does not match expected format for a GitHub repo",
                    repoUrl.getPath()
            );
            return Output.of(UnaryOperator.identity());
        }

        RepoIdentity repoIdentity = new RepoIdentity(pathMatcher.group("owner"), pathMatcher.group("repo"));

        return null;
    }

    @RequiredArgsConstructor
    private static class RepoIdentity {

        private final String owner;
        private final String repo;
    }
}
