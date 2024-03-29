package tech.kronicle.sdk.models.zipkin;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;
import tech.kronicle.sdk.models.ComponentState;

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
public class ZipkinState implements ComponentState {

    public static final String TYPE = "zipkin";

    String type = TYPE;
    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String pluginId;
    String id = null;
    @NotBlank
    String serviceName;
    @NotNull
    Boolean used;
    @NotNull
    List<@Valid ZipkinDependency> upstream;
    @NotNull
    List<@Valid ZipkinDependency> downstream;

    public ZipkinState(
            String pluginId, String serviceName,
            Boolean used,
            List<ZipkinDependency> upstream,
            List<ZipkinDependency> downstream
    ) {
        this.pluginId = pluginId;
        this.serviceName = serviceName;
        this.used = used;
        this.upstream = createUnmodifiableList(upstream);
        this.downstream = createUnmodifiableList(downstream);
    }
}
