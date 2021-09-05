package tech.kronicle.sdk.models.todos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class ToDo {

    String file;
    String description;
}
