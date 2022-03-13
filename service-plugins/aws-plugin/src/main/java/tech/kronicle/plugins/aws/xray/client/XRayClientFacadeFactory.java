package tech.kronicle.plugins.aws.xray.client;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class XRayClientFacadeFactory {

    private final XRayClientFactory clientFactory;
    
    public XRayClientFacade createXRayClientFacade(AwsProfileAndRegion profileAndRegion) {
        return new XRayClientFacade(clientFactory.createXRayClient(profileAndRegion));
    }
}
