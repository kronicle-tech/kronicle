package tech.kronicle.plugins.aws.utils;

import tech.kronicle.plugins.aws.config.AwsProfileConfig;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.utils.MapCollectors;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public final class ProfileUtils {

    public static <T> List<T> processProfilesToList(
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

    public static <T> Map<AwsProfileAndRegion, T> processProfilesToMap(
            List<AwsProfileConfig> profiles,
            Function<AwsProfileAndRegion, T> processor
    ) {
        if (isNull(profiles)) {
            return Map.of();
        }

        return profiles.stream()
                .flatMap(profile -> getRegions(profile).stream()
                        .map(region -> new AwsProfileAndRegion(profile, region))
                        .map(profileAndRegion -> Map.entry(profileAndRegion, processor.apply(profileAndRegion)))
                )
                .collect(MapCollectors.toMap());
    }

    private static List<String> getRegions(AwsProfileConfig profile) {
        return Optional.ofNullable(profile.getRegions()).orElse(List.of());
    }

    private ProfileUtils() {
    }
}
