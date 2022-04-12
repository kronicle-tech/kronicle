package tech.kronicle.plugins.aws.synthetics.client;

import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.synthetics.models.SyntheticsCanaryLastRun;

import java.util.List;

public interface SyntheticsClientFacade extends AutoCloseable {

    List<SyntheticsCanaryLastRun> describeCanariesLastRun(
            AwsProfileAndRegion profileAndRegion,
            List<String> canaryNames
    );

    @Override
    void close();
}
