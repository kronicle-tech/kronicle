package tech.kronicle.sdk.models.git;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.utils.ListUtils;

import java.time.LocalDateTime;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class GitRepo {

    LocalDateTime firstCommitTimestamp;
    LocalDateTime lastCommitTimestamp;
    Integer commitCount;
    List<Identity> authors;
    List<Identity> committers;
    // TODO: Remove concept of author count
    Integer authorCount;
    // TODO: Remove committer count and use committer list instead
    Integer committerCount;

    public GitRepo(LocalDateTime firstCommitTimestamp, LocalDateTime lastCommitTimestamp, Integer commitCount, List<Identity> authors,
            List<Identity> committers, Integer authorCount, Integer committerCount) {
        this.firstCommitTimestamp = firstCommitTimestamp;
        this.lastCommitTimestamp = lastCommitTimestamp;
        this.commitCount = commitCount;
        this.authors = createUnmodifiableList(authors);
        this.committers = createUnmodifiableList(committers);
        this.authorCount = authorCount;
        this.committerCount = committerCount;
    }
}
