package tech.kronicle.plugins.repostate;

import org.pf4j.Extension;
import tech.kronicle.pluginapi.scanners.ComponentScanner;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.Repo;

import java.util.Map;
import java.util.function.UnaryOperator;

import static java.util.Objects.isNull;
import static tech.kronicle.utils.MapCollectors.toUnmodifiableMap;

@Extension
public class RepoStateScanner extends ComponentScanner {

    private Map<String, Repo> repoUrlAndRepoMap;

    @Override
    public String description() {
        return "Copies state from repos to the components using those repos";
    }

    @Override
    public void refresh(ComponentMetadata componentMetadata) {
        repoUrlAndRepoMap = componentMetadata.getRepos().stream()
                .map(repo -> Map.entry(repo.getUrl(), repo))
                .collect(toUnmodifiableMap());
    }

    @Override
    public Output<Void> scan(Component input) {
        if (isNull(input.getRepo())) {
            return Output.of(UnaryOperator.identity());
        }

        Repo repo = repoUrlAndRepoMap.get(input.getRepo().getUrl());

        if (isNull(repo) || isNull(repo.getState())) {
            return Output.of(UnaryOperator.identity());
        }

        return Output.of(component -> component.withUpdatedState(
                state -> state.merge(repo.getState())
        ));
    }
}
