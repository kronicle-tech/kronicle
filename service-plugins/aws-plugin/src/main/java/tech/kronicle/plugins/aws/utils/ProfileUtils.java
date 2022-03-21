package tech.kronicle.plugins.aws.utils;

import tech.kronicle.plugins.aws.config.AwsProfileConfig;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public final class ProfileUtils {

    public static <T> List<T> processProfiles(
            List<AwsProfileConfig> profiles,
            Function<AwsProfileAndRegion, List<T>> processor,
            boolean includeGlobal
    ) {
        if (isNull(profiles)) {
            return List.of();
        }

        return profiles.stream()
                .flatMap(profile -> getRegions(profile, includeGlobal).stream()
                        .map(region -> new AwsProfileAndRegion(profile, region))
                        .map(processor)
                )
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static List<String> getRegions(AwsProfileConfig profile, boolean includeGlobal) {
        List<String> regions = new ArrayList<>();
        if (nonNull(profile.getRegions())) {
            regions.addAll(profile.getRegions());
        }
        if (includeGlobal) {
            regions.add(null);
        }
        return regions;
    }

    private ProfileUtils() {
    }
}
