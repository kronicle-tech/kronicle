package tech.kronicle.plugins.aws.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
public class AwsProfileConfig {

    String accessKeyId;
    String secretAccessKey;
    @NotEmpty
    List<@NotBlank String> regions;
    @NotBlank
    String environmentId;

    public AwsProfileConfig(
            String accessKeyId,
            String secretAccessKey,
            List<String> regions,
            String environmentId
    ) {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.regions = createUnmodifiableList(regions);
        this.environmentId = environmentId;
    }
}
