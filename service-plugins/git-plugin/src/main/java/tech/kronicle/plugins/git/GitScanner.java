package tech.kronicle.plugins.git;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.sdk.models.ScannerError;
import tech.kronicle.sdk.models.git.GitRepo;
import tech.kronicle.sdk.models.git.Identity;
import tech.kronicle.plugins.git.config.GitConfig;
import tech.kronicle.pluginapi.git.GitCloner;
import tech.kronicle.pluginapi.scanners.RepoScanner;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.pluginutils.scanners.services.ThrowableToScannerErrorMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@Extension
@RequiredArgsConstructor
public class GitScanner extends RepoScanner {

    private final GitCloner gitCloner;
    private final ThrowableToScannerErrorMapper throwableToScannerErrorMapper;
    private final GitConfig config;

    @Override
    public String id() {
        return "git";
    }

    @Override
    public String description() {
        return "If a component includes a repo URL, the scanner will create a local clone of the component's Git repo.  The local Git repo clones are "
                + "typically used as an input to other scanners";
    }

    @Override
    public Output<Codebase> scan(Repo input) {
        if (input.getUrl().startsWith("https://github.com/DecisionTechnologies/")) {
            Path repoDir = Path.of(config.getReposDir()).resolve("empty");
            try {
                Files.createDirectories(repoDir);
            } catch (IOException e) {
                return Output.of(new ScannerError(id(), "Failed to scan Git repo", throwableToScannerErrorMapper.map(id(), e)));
            }
            Codebase codebase = new Codebase(input, repoDir);
            return Output.of(UnaryOperator.identity(), codebase);
        }

        Path repoDir = gitCloner.cloneOrPullRepo(input.getUrl());

        try (Git git = Git.open(repoDir.toFile())) {
            LocalDateTime firstCommitTimestamp = getFirstCommitTimestamp(git);
            LocalDateTime lastCommitTimestamp = getLastCommitTimestamp(git);
            CommitStats commitStats = getCommitStats(git);

            Codebase codebase = new Codebase(input, repoDir);
            GitRepo gitRepo = new GitRepo(firstCommitTimestamp, lastCommitTimestamp, commitStats.commitCount, commitStats.authors, commitStats.committers,
                    commitStats.authors.size(), commitStats.committers.size());
            return Output.of(component -> component.withGitRepo(gitRepo), codebase);
        } catch (Exception e) {
            return Output.of(new ScannerError(id(), "Failed to scan Git repo", throwableToScannerErrorMapper.map(id(), e)));
        }
    }

    private LocalDateTime getFirstCommitTimestamp(Git git) throws IOException {
        Repository repository = git.getRepository();
        LocalDateTime firstCommitTimestamp = null;

        try (RevWalk revWalk = new RevWalk(repository)) {
            AnyObjectId headId = repository.resolve(Constants.HEAD);

            if (nonNull(headId)) {
                RevCommit headCommit = revWalk.parseCommit(headId);
                revWalk.sort(RevSort.REVERSE);
                revWalk.markStart(headCommit);
                RevCommit firstCommit = revWalk.next();
                firstCommitTimestamp = getCommitTime(firstCommit);
            }
        }

        return firstCommitTimestamp;
    }

    private LocalDateTime getLastCommitTimestamp(Git git) throws IOException {
        Repository repository = git.getRepository();
        LocalDateTime lastCommitTimestamp = null;

        try (RevWalk revWalk = new RevWalk(repository)) {
            AnyObjectId headId = repository.resolve(Constants.HEAD);

            if (nonNull(headId)) {
                RevCommit headCommit = revWalk.parseCommit(headId);
                revWalk.markStart(headCommit);
                RevCommit lastCommit = revWalk.next();
                lastCommitTimestamp = getCommitTime(lastCommit);
            }
        }

        return lastCommitTimestamp;
    }

    private CommitStats getCommitStats(Git git) throws GitAPIException, IOException {
        Iterable<RevCommit> commits = git.log().all().call();
        int commitCount = 0;
        Map<String, Identity> authors = new HashMap<>();
        Map<String, Identity> committers = new HashMap<>();

        for (RevCommit commit : commits) {
            addIdentity(authors, commit, RevCommit::getAuthorIdent);
            addIdentity(committers, commit, RevCommit::getCommitterIdent);
            commitCount++;
        }

        return new CommitStats(commitCount, getIdentities(authors), getIdentities(committers));
    }

    private void addIdentity(Map<String, Identity> identities, RevCommit commit, Function<RevCommit, PersonIdent> personIdentGetter) {
        PersonIdent personIdent = personIdentGetter.apply(commit);
        String emailAddress = personIdent.getEmailAddress();
        Identity identity = identities.get(emailAddress);
        if (isNull(identity)) {
            LocalDateTime commitTime = getCommitTime(commit);
            identity = new Identity(
                    List.of(personIdent.getName()),
                    emailAddress,
                    1,
                    commitTime,
                    commitTime);
        } else {
            identity = identity.withNames(addIdentityName(identity, personIdent))
                    .withCommitCount(identity.getCommitCount() + 1)
                    .withFirstCommitTimestamp(getFirstCommitTimestamp(identity, commit))
                    .withLastCommitTimestamp(getLastCommitTimestamp(identity, commit));
        }
        identities.put(emailAddress, identity);
    }

    private List<Identity> getIdentities(Map<String, Identity> identities) {
        return identities.values().stream()
                .sorted(Comparator.comparing(Identity::getCommitCount).reversed())
                .collect(Collectors.toList());
    }

    private List<String> addIdentityName(Identity identity, PersonIdent personIdent) {
        String name = personIdent.getName();
        if (identity.getNames().contains(name)) {
            return identity.getNames();
        }
        List<String> newList = new ArrayList<>(identity.getNames());
        newList.add(name);
        newList.sort(Comparator.naturalOrder());
        return newList;
    }

    private LocalDateTime getFirstCommitTimestamp(Identity identity, RevCommit commit) {
        LocalDateTime a = identity.getFirstCommitTimestamp();
        LocalDateTime b = getCommitTime(commit);
        return a.isBefore(b) ? a : b;
    }

    private LocalDateTime getLastCommitTimestamp(Identity identity, RevCommit commit) {
        LocalDateTime a = identity.getLastCommitTimestamp();
        LocalDateTime b = getCommitTime(commit);
        return a.isAfter(b) ? a : b;
    }

    private LocalDateTime getCommitTime(RevCommit commit) {
        return LocalDateTime.ofEpochSecond(commit.getCommitTime(), 0, ZoneOffset.UTC);
    }

    @Value
    private static class CommitStats {

        Integer commitCount;
        List<Identity> authors;
        List<Identity> committers;
    }
}
