package tech.kronicle.tracingprocessor.internal.models;

import lombok.Value;
import tech.kronicle.pluginapi.finders.models.GenericSpan;

@Value
public class SpanAndParentSpan {

    GenericSpan span;
    GenericSpan parentSpan;
}
