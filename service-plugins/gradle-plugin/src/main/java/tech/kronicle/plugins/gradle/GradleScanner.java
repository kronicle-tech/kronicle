package tech.kronicle.plugins.gradle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import tech.kronicle.common.StringEscapeUtils;
import tech.kronicle.pluginapi.scanners.CodebaseScanner;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.gradle.services.GradleDependenciesFinder;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentState;
import tech.kronicle.sdk.models.ScannerError;
import tech.kronicle.utils.ThrowableToScannerErrorMapper;

import javax.inject.Inject;
import java.time.Duration;
import java.util.List;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class GradleScanner extends CodebaseScanner {

    public static final String ID = "gradle";

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private final GradleDependenciesFinder dependenciesFinder;
    private final ThrowableToScannerErrorMapper throwableToScannerErrorMapper;

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String description() {
        return "Scans a component's codebase for any Gradle build scripts and collects information like Gradle version and software used";
    }

    @Override
    public String notes() {
        return "If the scanner finds Gradle build scripts, it will:\n"
                + "\n"
                + "* Find the version of Gradle wrapper used\n"
                + "* Find the names and versions of any Gradle plugins used"
                + "* Find the names and versions of any Java libraries used";
    }

    @Override
    public Output<Void, Component> scan(Codebase input) {
        log.info("Starting Gradle scan of codebase \"" + StringEscapeUtils.escapeString(input.getDir().toString()) + "\"");
        List<ComponentState> states;

        try {
            states = dependenciesFinder.findDependencies(input.getDir());
        } catch (Exception e) {
            return Output.ofError(
                    new ScannerError(
                            id(),
                            "Failed to scan codebase",
                            throwableToScannerErrorMapper.map(id(), e)
                    ),
                    CACHE_TTL
            );
        }

        return Output.ofTransformer(
                component -> component.addStates(states),
                CACHE_TTL
        );
    }
}
