package tech.kronicle.pluginapi.finders.models;

import lombok.Builder;
import lombok.Value;

import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@Builder
public class GenericTrace {

    List<GenericSpan> spans;

    public GenericTrace(List<GenericSpan> spans) {
        this.spans = createUnmodifiableList(spans);
    }
}
