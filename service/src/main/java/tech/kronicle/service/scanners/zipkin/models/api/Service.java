package tech.kronicle.service.scanners.zipkin.models.api;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class Service {

    String name;
    List<String> spanNames;

    public Service(String name, List<String> spanNames) {
        this.name = name;
        this.spanNames = createUnmodifiableList(spanNames);
    }
}
