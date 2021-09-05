package tech.kronicle.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class KeySoftware {

    @NotBlank
    String name;
    @NotEmpty
    List<@NotBlank String> versions;
}
