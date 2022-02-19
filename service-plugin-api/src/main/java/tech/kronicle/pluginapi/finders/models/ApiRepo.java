package tech.kronicle.pluginapi.finders.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class ApiRepo {

    @NotBlank
    String url;
    @NotNull
    Boolean hasComponentMetadataFile;
}
