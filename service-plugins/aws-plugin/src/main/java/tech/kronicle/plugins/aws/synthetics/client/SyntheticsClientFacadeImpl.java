package tech.kronicle.plugins.aws.synthetics.client;

import software.amazon.awssdk.services.synthetics.SyntheticsClient;
import software.amazon.awssdk.services.synthetics.model.CanaryLastRun;
import software.amazon.awssdk.services.synthetics.model.DescribeCanariesLastRunResponse;
import software.amazon.awssdk.services.synthetics.paginators.DescribeCanariesLastRunIterable;
import tech.kronicle.plugins.aws.client.BaseClientFacade;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.synthetics.models.SyntheticsCanaryLastRun;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SyntheticsClientFacadeImpl extends BaseClientFacade<SyntheticsClient>
        implements SyntheticsClientFacade {

    @Inject
    public SyntheticsClientFacadeImpl(SyntheticsClientFactory clientFactory) {
        super(clientFactory);
    }

    public List<SyntheticsCanaryLastRun> describeCanariesLastRun(
            AwsProfileAndRegion profileAndRegion,
            List<String> canaryNames
    ) {
        return mapCanariesLastRunsIterable(getClient(profileAndRegion)
                .describeCanariesLastRunPaginator(
                        builder -> builder
                                .names(canaryNames)
                                .build()
                ));
    }

    private List<SyntheticsCanaryLastRun> mapCanariesLastRunsIterable(DescribeCanariesLastRunIterable iterable) {
        return iterable.stream()
                .flatMap(this::mapCanariesLastRunsResponse)
                .collect(Collectors.toUnmodifiableList());
    }

    private Stream<SyntheticsCanaryLastRun> mapCanariesLastRunsResponse(DescribeCanariesLastRunResponse response) {
        return response.canariesLastRun().stream()
                .map(this::mapCanaryLastRun);
    }

    private SyntheticsCanaryLastRun mapCanaryLastRun(CanaryLastRun canaryLastRun) {
        return new SyntheticsCanaryLastRun(
                canaryLastRun.canaryName(),
                canaryLastRun.lastRun().status().stateAsString(),
                canaryLastRun.lastRun().status().stateReason(),
                canaryLastRun.lastRun().status().stateReasonCodeAsString(),
                LocalDateTime.ofInstant(canaryLastRun.lastRun().timeline().completed(), ZoneOffset.UTC)
        );
    }
}
