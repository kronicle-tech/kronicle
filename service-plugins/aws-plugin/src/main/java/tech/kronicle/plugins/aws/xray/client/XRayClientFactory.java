package tech.kronicle.plugins.aws.xray.client;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.xray.XRayClient;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.utils.AwsCredentialsProviderFactory;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class XRayClientFactory {

    private final AwsCredentialsProviderFactory credentialsProviderFactory;

    public XRayClient createXRayClient(AwsProfileAndRegion profileAndRegion) {
        return XRayClient.builder()
                .credentialsProvider(
                        credentialsProviderFactory.createCredentialsProvider(profileAndRegion)
                )
                .region(Region.of(profileAndRegion.getRegion()))
                .build();
    }
}
