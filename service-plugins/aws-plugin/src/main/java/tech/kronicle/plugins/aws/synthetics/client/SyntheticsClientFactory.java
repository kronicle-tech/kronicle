package tech.kronicle.plugins.aws.synthetics.client;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.synthetics.SyntheticsClient;
import tech.kronicle.plugins.aws.client.ClientFactory;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.utils.AwsCredentialsProviderFactory;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SyntheticsClientFactory implements ClientFactory<SyntheticsClient> {

    private final AwsCredentialsProviderFactory credentialsProviderFactory;

    public SyntheticsClient createClient(AwsProfileAndRegion profileAndRegion) {
        return SyntheticsClient.builder()
                .credentialsProvider(
                        credentialsProviderFactory.createCredentialsProvider(profileAndRegion)
                )
                .region(Region.of(profileAndRegion.getRegion()))
                .build();
    }
}
