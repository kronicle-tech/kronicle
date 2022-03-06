package tech.kronicle.plugins.gradle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import tech.kronicle.common.StringEscapeUtils;
import tech.kronicle.pluginapi.scanners.CodebaseScanner;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.ScannerError;
import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.SoftwareRepository;
import tech.kronicle.sdk.models.gradle.Gradle;
import tech.kronicle.utils.ThrowableToScannerErrorMapper;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class GradleScanner extends CodebaseScanner {

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
    public Output<Void> scan(Codebase input) {
        log.info("Starting Gradle scan of codebase \"" + StringEscapeUtils.escapeString(input.getDir().toString()) + "\"");
        GradleAnalysis gradleAnalysis;

        try {
            gradleAnalysis = gradleStaticAnalyzer.analyzeCodebase(input.getDir());
        } catch (Exception e) {
            return Output.of(new ScannerError(id(), "Failed to scan codebase", throwableToScannerErrorMapper.map(id(), e)));
        }

        return Output.of(component -> component.withGradle(new Gradle(gradleAnalysis.getGradleIsUsed()))
                .withSoftwareRepositories(replaceScannerItemsInList(
                        component.getSoftwareRepositories(),
                        setScannerIdOnSoftwareRepositories(gradleAnalysis.getSoftwareRepositories()))
                )
                .withSoftware(replaceScannerItemsInList(
                        component.getSoftware(),
                        setScannerIdOnSoftware(gradleAnalysis.getSoftware())
                )));
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
