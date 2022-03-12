package tech.kronicle.plugins.aws.xray.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.aws.config.AwsProfileConfig;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class XRayClientFacadeFactory {

    private final XRayClientFactory clientFactory;
    
    public XRayClientFacade createXRayClientFacade(AwsProfileConfig profile, String region) {
        return new XRayClientFacade(clientFactory.createXRayClient(profile, region));
    }
}
