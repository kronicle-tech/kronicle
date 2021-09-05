package tech.kronicle.sdk.models;

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
public class Software implements ObjectWithScannerId {

    @NotBlank
    String scannerId;
    @NotNull
    SoftwareType type;
    @NotNull
    SoftwareDependencyType dependencyType;
    @NotBlank
    String name;
    String version;
    String versionSelector;
    String packaging;
    SoftwareScope scope;
}
