package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class ComponentStateCheck {

    @NotEmpty
    String name;
    @NotNull
    ComponentStateCheckStatus status;
    @NotEmpty
    String statusMessage;
    List<@Valid Link> links;
    @NotNull
    LocalDateTime updateTimestamp;
}
