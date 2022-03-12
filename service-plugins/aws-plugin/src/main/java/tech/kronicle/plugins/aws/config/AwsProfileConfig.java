package tech.kronicle.plugins.aws.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@NonFinal
public class AwsProfileConfig {

    String accessKeyId;
    String secretAccessKey;
    List<String> regions;

    public AwsProfileConfig(String accessKeyId, String secretAccessKey, List<String> regions) {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.regions = createUnmodifiableList(regions);
    }
}
