package tech.kronicle.sdk.models.git;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.LocalDateTime;
import java.util.List;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class Identity {

    List<String> names;
    String emailAddress;
    Integer commitCount;
    LocalDateTime firstCommitTimestamp;
    LocalDateTime lastCommitTimestamp;
}
