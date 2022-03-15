package tech.kronicle.plugins.aws.xray.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.xray.client.XRayClientFacade;
import tech.kronicle.plugins.aws.xray.client.XRayClientFacadeFactory;
import tech.kronicle.plugins.aws.xray.models.XRayDependency;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static tech.kronicle.plugins.aws.utils.PageFetcher.fetchAllPages;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class XRayServiceGraphFetcher {

    private static final int X_RAY_DATA_RETENTION_IN_DAYS = 30;
    private static final int FETCH_INTERVAL_IN_HOURS = 6;
    private final XRayClientFacadeFactory clientFacadeFactory;
    private final Clock clock;

    public List<XRayDependency> getServiceGraph(AwsProfileAndRegion profileAndRegion) {
        try (XRayClientFacade clientFacade = clientFacadeFactory.createXRayClientFacade(profileAndRegion)) {
            Instant endTime = clock.instant();
            Instant currentStartTime = getStartTime(endTime);
            List<XRayDependency> dependencies = new ArrayList<>();

            while (currentStartTime.isBefore(endTime)) {
                Instant currentStartTime2 = currentStartTime;
                Instant currentEndTime = currentStartTime.plus(FETCH_INTERVAL_IN_HOURS, ChronoUnit.HOURS);

                dependencies.addAll(fetchAllPages(
                        nextToken -> clientFacade.getServiceGraph(currentStartTime2, currentEndTime, nextToken)
                ));

                currentStartTime = currentEndTime;
            }

            return dependencies;
        }
    }

    private Instant getStartTime(Instant endTime) {
        return endTime.minus(X_RAY_DATA_RETENTION_IN_DAYS, ChronoUnit.DAYS)
                .plus(FETCH_INTERVAL_IN_HOURS, ChronoUnit.HOURS);
    }
}
