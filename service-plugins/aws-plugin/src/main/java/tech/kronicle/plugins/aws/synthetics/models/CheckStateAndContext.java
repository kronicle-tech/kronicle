package tech.kronicle.plugins.aws.synthetics.models;

import lombok.Value;
import tech.kronicle.sdk.models.CheckState;

@Value
public class CheckStateAndContext {

    String environmentId;
    CheckState check;
}
