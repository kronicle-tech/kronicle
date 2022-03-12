package tech.kronicle.plugins.aws.xray.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.aws.config.AwsProfileConfig;
import tech.kronicle.plugins.aws.xray.models.XRayDependency;
import tech.kronicle.plugins.aws.xray.models.XRayServiceGraphPage;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class XRayServiceGraphFetcher {

    private static final int X_RAY_DATA_RETENTION_IN_DAYS = 30;
    private static final int FETCH_INTERVAL_IN_HOURS = 6;
    private final XRayClientFacadeFactory clientFacadeFactory;
    private final Clock clock;

    public List<XRayDependency> getServiceGraph(AwsProfileConfig profile, String region) {
        try (XRayClientFacade clientFacade = clientFacadeFactory.createXRayClientFacade(profile, region)) {
            Instant endTime = clock.instant();
            Instant currentStartTime = getStartTime(endTime);
            List<XRayDependency> dependencies = new ArrayList<>();

            while (currentStartTime.isBefore(endTime)) {
                Instant currentEndTime = currentStartTime.plus(FETCH_INTERVAL_IN_HOURS, ChronoUnit.HOURS);
                String nextToken;

                do {
                    XRayServiceGraphPage page = clientFacade.getServiceGraph(currentStartTime, currentEndTime, null);
                    dependencies.addAll(page.getDependencies());
                    nextToken = page.getNextPage();
                } while (nonNull(nextToken));

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
