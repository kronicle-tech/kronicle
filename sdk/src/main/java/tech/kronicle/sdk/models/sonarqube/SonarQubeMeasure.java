package tech.kronicle.sdk.models.sonarqube;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder(toBuilder = true)
public class SonarQubeMeasure {

    String metric;
    String value;
    Boolean bestValue;
}
