package tech.kronicle.plugins.aws.utils;

import tech.kronicle.plugins.aws.config.AwsProfileConfig;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public final class ProfileUtils {

    public static <T> List<T> processProfiles(
            List<AwsProfileConfig> profiles,
            Function<AwsProfileAndRegion, List<T>> processor
    ) {
        if (isNull(profiles)) {
            return List.of();
        }

        return profiles.stream()
                .flatMap(profile -> getRegions(profile).stream()
                        .map(region -> new AwsProfileAndRegion(profile, region))
                        .map(processor)
                )
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static List<String> getRegions(AwsProfileConfig profile) {
        return Optional.ofNullable(profile.getRegions()).orElse(List.of());
    }

    private ProfileUtils() {
    }
}
