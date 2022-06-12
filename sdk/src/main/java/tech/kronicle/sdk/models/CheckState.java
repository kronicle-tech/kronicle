package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class CheckState implements ComponentEnvironmentState {

    public static final String TYPE = "check";

    String type = TYPE;
    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String pluginId;
    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String environmentId;
    @NotEmpty
    String name;
    String description;
    String avatarUrl;
    @NotNull
    ComponentStateCheckStatus status;
    @NotEmpty
    String statusMessage;
    List<@Valid Link> links;
    @NotNull
    LocalDateTime updateTimestamp;

    public CheckState(
            String pluginId,
            String environmentId,
            String name,
            String description,
            String avatarUrl,
            ComponentStateCheckStatus status,
            String statusMessage,
            List<Link> links,
            LocalDateTime updateTimestamp
    ) {
        this.pluginId = pluginId;
        this.environmentId = environmentId;
        this.name = name;
        this.description = description;
        this.avatarUrl = avatarUrl;
        this.status = status;
        this.statusMessage = statusMessage;
        this.links = createUnmodifiableList(links);
        this.updateTimestamp = updateTimestamp;
    }
}
