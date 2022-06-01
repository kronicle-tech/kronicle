package tech.kronicle.plugins.gradle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import tech.kronicle.common.StringEscapeUtils;
import tech.kronicle.gradlestaticanalyzer.GradleAnalysis;
import tech.kronicle.gradlestaticanalyzer.GradleStaticAnalyzer;
import tech.kronicle.pluginapi.scanners.CodebaseScanner;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.*;
import tech.kronicle.sdk.models.gradle.GradleState;
import tech.kronicle.utils.ThrowableToScannerErrorMapper;

import javax.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class GradleScanner extends CodebaseScanner {

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private final GradleStaticAnalyzer gradleStaticAnalyzer;
    private final ThrowableToScannerErrorMapper throwableToScannerErrorMapper;

    @Override
    public String id() {
        return "gradle";
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
        GradleAnalysis gradleAnalysis;

        try {
            gradleAnalysis = gradleStaticAnalyzer.analyzeCodebase(input.getDir());
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
                component -> component
                        .addState(new GradleState(GradlePlugin.ID, gradleAnalysis.getGradleIsUsed()))
                        .addState(new SoftwareRepositoriesState(
                                GradlePlugin.ID,
                                setScannerIdOnSoftwareRepositories(gradleAnalysis.getSoftwareRepositories())
                        ))
                        .addState(new SoftwaresState(
                                GradlePlugin.ID,
                                setScannerIdOnSoftware(gradleAnalysis.getSoftware())
                        )),
                CACHE_TTL
        );
    }

    private List<SoftwareRepository> setScannerIdOnSoftwareRepositories(List<SoftwareRepository> softwareRepositories) {
        return softwareRepositories.stream()
                .map(it -> it.withScannerId(id()))
                .collect(Collectors.toList());
    }

    private List<Software> setScannerIdOnSoftware(List<Software> software) {
        return software.stream()
                .map(it -> it.withScannerId(id()))
                .collect(Collectors.toList());
    }
}
