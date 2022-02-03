package tech.kronicle.sdk.models.nodejs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class NodeJs {

    Boolean used;
}
