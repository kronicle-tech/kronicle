package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class SonarQubeProjectsState implements ComponentState {

    public static final String TYPE = "sonarqube-projects";

    String type = TYPE;
    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String pluginId;
    @NotNull
    List<@Valid SonarQubeProject> sonarQubeProjects;

    public SonarQubeProjectsState(String pluginId, List<@Valid SonarQubeProject> sonarQubeProjects) {
        this.pluginId = pluginId;
        this.sonarQubeProjects = createUnmodifiableList(sonarQubeProjects);
    }
}
