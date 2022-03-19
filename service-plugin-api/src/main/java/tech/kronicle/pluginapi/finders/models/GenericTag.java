package tech.kronicle.pluginapi.finders.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class GenericTag {

    String key;
    String value;
}
