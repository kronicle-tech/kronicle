package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;

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
public class KeySoftwaresState implements ComponentState {

    public static final String TYPE = "key-softwares";

    String type = TYPE;
    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String pluginId;
    String id = null;
    @NotNull
    List<@Valid KeySoftware> keySoftwares;

    public KeySoftwaresState(String pluginId, List<@Valid KeySoftware> keySoftwares) {
        this.pluginId = pluginId;
        this.keySoftwares = createUnmodifiableList(keySoftwares);
    }
}
