package tech.kronicle.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class SoftwareRepositoriesState implements ComponentState {

    public static final String TYPE = "software-repositories";

    String type = TYPE;
    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String pluginId;
    String id = null;
    @NotNull
    List<@Valid SoftwareRepository> softwareRepositories;

    public SoftwareRepositoriesState(String pluginId, List<@Valid SoftwareRepository> softwareRepositories) {
        this.pluginId = pluginId;
        this.softwareRepositories = createUnmodifiableList(softwareRepositories);
    }
}
