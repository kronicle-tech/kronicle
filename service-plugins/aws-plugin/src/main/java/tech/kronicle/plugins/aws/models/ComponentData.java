package tech.kronicle.plugins.aws.models;

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
public class ComponentData {

    List<String> logGroupNamePatterns;
    String logLevelFieldName;

    public ComponentData(List<String> logGroupNamePatterns, String logLevelFieldName) {
        this.logGroupNamePatterns = createUnmodifiableList(logGroupNamePatterns);
        this.logLevelFieldName = logLevelFieldName;
    }
}
