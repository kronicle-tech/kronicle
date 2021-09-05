package tech.kronicle.sdk.models.sonarqube;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class SonarQubeProject {

    @NotBlank
    String key;
    @NotBlank
    String name;
    @NotBlank
    String url;
    LocalDateTime lastCommitTimestamp;
    List<@Valid SonarQubeMeasure> measures;

    public SonarQubeProject(String key, String name, String url, LocalDateTime lastCommitTimestamp, List<SonarQubeMeasure> measures) {
        this.key = key;
        this.name = name;
        this.url = url;
        this.lastCommitTimestamp = lastCommitTimestamp;
        this.measures = createUnmodifiableList(measures);
    }
}
