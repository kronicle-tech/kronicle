package tech.kronicle.service.scanners.sonarqube.models.api;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class GetComponentMeasuresResponse {

    Component component;
}
