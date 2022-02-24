package tech.kronicle.service.tests.dropwizard;

import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.KeySoftware;
import tech.kronicle.sdk.models.Priority;
import tech.kronicle.sdk.models.TestOutcome;
import tech.kronicle.sdk.models.TestResult;
import tech.kronicle.service.tests.ComponentTest;
import tech.kronicle.service.tests.models.TestContext;
import lombok.RequiredArgsConstructor;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.DefaultVersionComparator;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.Version;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.VersionParser;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@org.springframework.stereotype.Component
@RequiredArgsConstructor
public class DropwizardVersionTest extends ComponentTest {

    private static final Object DROPWIZARD_NAME = "dropwizard";
    private final VersionParser versionParser = new VersionParser();
    private final Comparator<Version> versionComparator = new DefaultVersionComparator().asVersionComparator();
    private final Version minimumVersion = versionParser.transform("2.0.0");

    @Override
    public String description() {
        return "For components using the Dropwizard web framework, checks that they are using a recent and supported version of Dropwizard";
    }

    @Override
    public Priority priority() {
        return Priority.HIGH;
    }

    @Override
    public TestResult test(Component input, TestContext testContext) {
        List<KeySoftware> keySoftware = input.getKeySoftware();

        Optional<KeySoftware> optionalDropwizard = keySoftware.stream()
                .filter(item -> Objects.equals(item.getName(), DROPWIZARD_NAME))
                .findFirst();

        Optional<TestResult> failTestResults = optionalDropwizard.map(this::testVersions);
        return failTestResults.orElseGet(this::createNotApplicableTestResult);
    }

    private TestResult testVersions(KeySoftware dropwizard) {
        List<TestResult> testResults = getTestResults(dropwizard);

        return getFirstTestResultByOutcome(testResults, TestOutcome.FAIL)
                .orElseGet(() -> getFirstTestResultByOutcome(testResults, TestOutcome.PASS)
                        .orElseGet(() -> createFailTestResult("Versions were missing for Dropwizard `key software`")));
    }

    private List<TestResult> getTestResults(KeySoftware dropwizard) {
        return streamVersionsFromHighestToLowest(dropwizard)
                .map(this::testVersion)
                .collect(Collectors.toList());
    }

    private Stream<Version> streamVersionsFromHighestToLowest(KeySoftware dropwizard) {
        return dropwizard.getVersions().stream()
                    .map(versionParser::transform)
                    .sorted(versionComparator);
    }

    private Optional<TestResult> getFirstTestResultByOutcome(List<TestResult> testResults, TestOutcome outcome) {
        return testResults.stream().filter(testResult -> Objects.equals(testResult.getOutcome(), outcome)).findFirst();
    }

    private TestResult testVersion(Version version) {
        if (versionComparator.compare(version, minimumVersion) < 0) {
            return createFailTestResult(
                    String.format("Component is using very old and unsupported version `%s` of the Dropwizard framework.  "
                            + "Should be using at least version `%s`.  ", version.getSource(), minimumVersion.getSource()));
        } else {
            return createPassTestResult(
                    String.format("Component is using supported version `%s` of the Dropwizard framework which is equal to or greater than the minimum "
                            + "supported version of `%s`.  ", version.getSource(), minimumVersion.getSource()));
        }
    }
}
