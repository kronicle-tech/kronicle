package tech.kronicle.plugins.zipkin.models.api;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Map;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;
import static tech.kronicle.sdk.utils.MapUtils.createUnmodifiableMap;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class Span {

    String traceId;
    String name;
    String parentId;
    String id;
    String kind;
    Long timestamp;
    Long duration;
    Boolean debug;
    Boolean shared;
    Endpoint localEndpoint;
    Endpoint remoteEndpoint;
    List<Annotation> annotations;
    Map<String, String> tags;

    public Span(String traceId, String name, String parentId, String id, String kind, Long timestamp, Long duration, Boolean debug, Boolean shared,
            Endpoint localEndpoint, Endpoint remoteEndpoint, List<Annotation> annotations, Map<String, String> tags) {
        this.traceId = traceId;
        this.name = name;
        this.parentId = parentId;
        this.id = id;
        this.kind = kind;
        this.timestamp = timestamp;
        this.duration = duration;
        this.debug = debug;
        this.shared = shared;
        this.localEndpoint = localEndpoint;
        this.remoteEndpoint = remoteEndpoint;
        this.annotations = createUnmodifiableList(annotations);
        this.tags = createUnmodifiableMap(tags);
    }
}
