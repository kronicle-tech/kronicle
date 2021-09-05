package tech.kronicle.sdk.models.zipkin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class Zipkin {

    @NotBlank
    String serviceName;
    Boolean used;
    List<@Valid ZipkinDependency> upstream;
    List<@Valid ZipkinDependency> downstream;
}
