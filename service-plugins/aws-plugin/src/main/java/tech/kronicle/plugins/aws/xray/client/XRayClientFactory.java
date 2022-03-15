package tech.kronicle.plugins.aws.xray.client;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.xray.XRayClient;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;

import static tech.kronicle.plugins.aws.utils.StaticCredentialsUtils.createStaticCredentialsProvider;

public class XRayClientFactory {

    public XRayClient createXRayClient(AwsProfileAndRegion profileAndRegion) {
        return XRayClient.builder()
                .credentialsProvider(createStaticCredentialsProvider(profileAndRegion.getProfile()))
                .region(Region.of(profileAndRegion.getRegion()))
                .build();
    }
}
