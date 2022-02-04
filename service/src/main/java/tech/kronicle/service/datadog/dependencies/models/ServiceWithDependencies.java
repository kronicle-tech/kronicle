package tech.kronicle.service.datadog.dependencies.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@Builder
@Jacksonized
public class ServiceWithDependencies {

    public ServiceWithDependencies(List<String> calls) {
        this.calls = createUnmodifiableList(calls);
    }

    List<String> calls;
}
