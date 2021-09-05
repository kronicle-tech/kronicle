package tech.kronicle.service.scanners.sonarqube.models.api;

import tech.kronicle.sdk.models.sonarqube.SonarQubeMeasure;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class Component {

    String organization;
    String id;
    String key;
    String name;
    ComponentQualifier qualifier;
    String project;
    List<SonarQubeMeasure> measures;
}
