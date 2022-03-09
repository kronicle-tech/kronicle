package tech.kronicle.plugins.aws.xray.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.aws.xray.models.Service;
import tech.kronicle.plugins.aws.xray.models.ServiceGraphPage;

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
    private final XRayClientFacade clientFacade;
    private final Clock clock;

    public List<Service> getServiceGraph() {
        Instant endTime = clock.instant();
        Instant startTime = endTime.minus(X_RAY_DATA_RETENTION_IN_DAYS, ChronoUnit.DAYS);
        Instant currentStartTime = startTime;
        List<Service> services = new ArrayList<>();

        while (currentStartTime.isBefore(endTime)) {
            Instant currentEndTime = currentStartTime.plus(FETCH_INTERVAL_IN_HOURS, ChronoUnit.HOURS);
            String nextToken;

            do {
                ServiceGraphPage page = clientFacade.getServiceGraph(currentStartTime, currentEndTime, null);
                services.addAll(page.getServices());
                nextToken = page.getNextPage();
            } while (nonNull(nextToken));

            currentStartTime = currentEndTime;
        }

        return services;
    }
}
