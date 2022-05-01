package tech.kronicle.plugins.bitbucketserver;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.finders.RepoFinder;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.plugins.bitbucketserver.client.BitbucketServerClient;

import javax.inject.Inject;
import java.time.Duration;
import java.util.List;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class BitbucketServerRepoFinder extends RepoFinder {

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private final BitbucketServerClient client;

    @Override
    public String description() {
        return "Find repositories hosted by Bitbucket Server.  ";
    }

    @Override
    public Output<List<Repo>, Void> find(Void ignored) {
        return Output.ofOutput(client.getNormalRepos(), CACHE_TTL);
    }
}
