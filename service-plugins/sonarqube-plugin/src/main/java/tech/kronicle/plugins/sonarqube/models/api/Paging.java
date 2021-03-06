package tech.kronicle.plugins.sonarqube.models.api;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class Paging {

    Integer pageIndex;
    Integer pageSize;
    Integer total;
}
