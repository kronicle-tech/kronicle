package tech.kronicle.service.scanners.gradle;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.SoftwareDependencyType;
import tech.kronicle.sdk.models.SoftwareRepository;
import tech.kronicle.sdk.models.SoftwareRepositoryScope;
import tech.kronicle.sdk.models.SoftwareRepositoryType;
import tech.kronicle.sdk.models.SoftwareScope;
import tech.kronicle.sdk.models.SoftwareType;
import tech.kronicle.service.config.DownloadCacheConfig;
import tech.kronicle.service.config.UrlExistsCacheConfig;
import tech.kronicle.service.constants.Comparators;
import tech.kronicle.service.scanners.BaseCodebaseScannerTest;
import tech.kronicle.service.scanners.gradle.config.GradleConfig;
import tech.kronicle.service.scanners.gradle.internal.constants.MavenPackagings;
import tech.kronicle.service.scanners.models.Codebase;
import tech.kronicle.service.scanners.models.Output;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "download-cache.dir=build/test-data/tech.kronicle.service.scanners.gradle.GradleScannerTest/download-cache",
        "url-exists-cache.dir=build/test-data/tech.kronicle.service.scanners.gradle.GradleScannerTest/url-exists-cache",
        "gradle.pom-cache-dir=build/test-data/tech.kronicle.service.scanners.gradle.GradleScannerTest/gradle/pom-cache"
})
@ContextConfiguration(classes = GradleScannerTestConfiguration.class)
@EnableConfigurationProperties(value = {DownloadCacheConfig.class, UrlExistsCacheConfig.class, GradleConfig.class})
public class GradleScannerTest extends BaseCodebaseScannerTest {

    private static final String SCANNER_ID = "gradle";
    private static final SoftwareRepository GRADLE_PLUGIN_PORTAL_REPOSITORY = SoftwareRepository
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareRepositoryType.MAVEN)
            .url("https://plugins.gradle.org/m2/")
            .safe(true)
            .build();
    private static final SoftwareRepository JCENTER_REPOSITORY = SoftwareRepository
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareRepositoryType.MAVEN)
            .url("https://jcenter.bintray.com/")
            .safe(true)
            .build();
    private static final SoftwareRepository GOOGLE_REPOSITORY = SoftwareRepository
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareRepositoryType.MAVEN)
            .url("https://dl.google.com/dl/android/maven2/")
            .safe(false)
            .build();
    private static final SoftwareRepository MAVEN_CENTRAL_REPOSITORY = SoftwareRepository
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareRepositoryType.MAVEN)
            .url("https://repo.maven.apache.org/maven2/")
            .safe(true)
            .build();
    private static final Software SPRING_CLOUD_STARTER_ZIPKIN_2_2_5_RELEASE = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.springframework.cloud:spring-cloud-starter-zipkin")
            .version("2.2.5.RELEASE")
            .build();
    private static final Software SPRING_BOOT_GRADLE_PLUGIN_2_3_4_RELEASE = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.springframework.boot:spring-boot-gradle-plugin")
            .version("2.3.4.RELEASE")
            .scope(SoftwareScope.BUILDSCRIPT)
            .build();
    private static final Software DEPENDENCY_CHECK_GRADLE_6_0_2_BUILDSCRIPT = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.owasp:dependency-check-gradle")
            .version("6.0.2")
            .scope(SoftwareScope.BUILDSCRIPT)
            .build();
    private static final Software GRADLE_WRAPPER_6_7 = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.TOOL)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("gradle-wrapper")
            .version("6.7")
            .build();
    private static final Software SPRING_BOOT_STARTER_ACTUATOR_2_3_4_RELEASE = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.springframework.boot:spring-boot-starter-actuator")
            .version("2.3.4.RELEASE")
            .build();
    private static final Software LOMBOK_1_18_16 = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.projectlombok:lombok")
            .version("1.18.16")
            .build();
    private static final Software SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.springframework.boot:spring-boot-starter-web")
            .version("2.3.4.RELEASE")
            .build();
    private static final Software SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE_SOURCES = SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE
            .toBuilder()
            .packaging("sources")
            .build();
    private static final Software SPRING_BOOT_STARTER_WEB_2_0_9_RELEASE = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.springframework.boot:spring-boot-starter-web")
            .version("2.0.9.RELEASE")
            .build();
    private static final Software HIBERNATE_VALIDATOR_4_1_0_FINAL = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.hibernate.validator:hibernate-validator")
            .version("6.0.0.Final")
            .build();
    private static final Software HIBERNATE_VALIDATOR_6_1_6_FINAL = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.hibernate.validator:hibernate-validator")
            .version("6.1.6.Final")
            .build();
    private static final Software JTDS_1_3_1 = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("net.sourceforge.jtds:jtds")
            .version("1.3.1")
            .build();
    private static final Software JAVA_PLUGIN = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.GRADLE_PLUGIN)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("java")
            .build();
    private static final Software GROOVY_PLUGIN = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.GRADLE_PLUGIN)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("groovy")
            .build();
    private static final Software SPRING_DEPENDENCY_MANAGEMENT_PLUGIN_1_0_10_RELEASE = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.GRADLE_PLUGIN)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("io.spring.dependency-management")
            .version("1.0.10.RELEASE")
            .build();
    private static final Software SPRING_BOOT_PLUGIN_2_3_4_RELEASE = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.GRADLE_PLUGIN)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.springframework.boot")
            .version("2.3.4.RELEASE")
            .build();
    private static final Software DEPENDENCY_CHECK_PLUGIN = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.GRADLE_PLUGIN)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.owasp.dependencycheck.gradle.DependencyCheckPlugin")
            .build();
    @Autowired
    private GradleScanner underTest;

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("gradle");
    }


    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Scans a component's codebase for any Gradle build scripts.  Collections information like Gradle version and "
                + "software used");
    }

    @Test
    public void notesShouldReturnTheNotesForTheScanner() {
        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isEqualTo("If the scanner finds Gradle build scripts, it will:\n"
                + "\n"
                + "* Find the version of Gradle wrapper used\n"
                + "* Find the names and versions of any Gradle plugins used"
                + "* Find the names and versions of any Java libraries used");
    }

    @Test
    public void shouldHandleNone() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("None"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsNotUsed(component);
        assertThat(getSoftwareRepositories(component)).isEmpty();
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    private void assertThatGradleIsNotUsed(Component component) {
        assertThat(component.getGradle()).isNotNull();
        assertThat(component.getGradle().getUsed()).isFalse();
    }

    private List<SoftwareRepository> getSoftwareRepositories(Component component) {
        return component.getSoftwareRepositories().stream()
                .sorted(Comparators.SOFTWARE_REPOSITORIES)
                .collect(Collectors.toList());
    }

    private Map<SoftwareGroup, List<Software>> getSoftwareGroups(Component component) {
        Map<SoftwareGroup, List<Software>> softwareGroups = component
                .getSoftware()
                .stream()
                .sorted(Comparators.SOFTWARE)
                .collect(Collectors.groupingBy(this::softwareClassifier));

        List<Software> bomSoftware = softwareGroups.get(SoftwareGroup.BOM);

        if (nonNull(bomSoftware)) {
            bomSoftware.forEach(this::assertSoftwareFieldsAreValid);
        }

        List<Software> transitiveSoftware = softwareGroups.get(SoftwareGroup.TRANSITIVE);

        if (nonNull(transitiveSoftware)) {
            transitiveSoftware.forEach(this::assertSoftwareFieldsAreValid);
        }

        return softwareGroups;
    }

    private SoftwareGroup softwareClassifier(Software software) {
        if (Objects.equals(software.getPackaging(), MavenPackagings.BOM)) {
            return SoftwareGroup.BOM;
        } else if (Objects.equals(software.getDependencyType(), SoftwareDependencyType.DIRECT)) {
            return SoftwareGroup.DIRECT;
        } else if (Objects.equals(software.getDependencyType(), SoftwareDependencyType.TRANSITIVE)) {
            return SoftwareGroup.TRANSITIVE;
        } else {
            throw new RuntimeException("Unexpected software dependency type " + software.getDependencyType());
        }
    }

    private void assertSoftwareFieldsAreValid(Software item) {
        assertThat(item.getScannerId()).isNotEmpty();
        assertThat(item.getType()).isNotNull();
        assertThat(item.getDependencyType()).isNotNull();
        assertThat(item.getName()).isNotEmpty();
        assertThat(item.getVersion()).isNotEmpty();
        assertThat(item.getVersion()).doesNotContain("$");
    }

    @Test
    public void shouldHandleEmpty() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("Empty"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).isEmpty();
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    private void assertThatGradleIsUsed(Component component) {
        assertThat(component.getGradle()).isNotNull();
        assertThat(component.getGradle().getUsed()).isTrue();
    }

    @Test
    public void shouldHandleRootProjectBuiltInProperties() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("RootProjectBuiltInProperties"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).isEmpty();
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleProjectBuiltInProperties() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("ProjectBuiltInProperties"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).isEmpty();
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependencyClass() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("DependencyClass"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependency() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("Dependency"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependencyPackaging() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("DependencyPackaging"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                JAVA_PLUGIN,
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE_SOURCES);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependencyDynamicVersion() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("DependencyDynamicVersion"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_WEB_2_0_9_RELEASE.withVersionSelector("2.0+"),
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(6);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependencyPomXmlWithoutNamespace() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("DependencyPomXmlWithoutNamespace"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                JTDS_1_3_1,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(1);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependencyFollowRedirect() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("DependencyFollowRedirect"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                GRADLE_PLUGIN_PORTAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependencyList() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("DependencyList"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_ACTUATOR_2_3_4_RELEASE,
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(7);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleApplyPlugin() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("ApplyPlugin"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                MAVEN_CENTRAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_GRADLE_PLUGIN_2_3_4_RELEASE,
                JAVA_PLUGIN,
                SPRING_BOOT_PLUGIN_2_3_4_RELEASE);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).hasSize(29);
    }

    @Test
    public void shouldHandleApplyPluginClass() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("ApplyPluginClass"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                MAVEN_CENTRAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT));
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                DEPENDENCY_CHECK_GRADLE_6_0_2_BUILDSCRIPT,
                DEPENDENCY_CHECK_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(4);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleApplyPluginImportedClass() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("ApplyPluginImportedClass"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                MAVEN_CENTRAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT));
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                DEPENDENCY_CHECK_GRADLE_6_0_2_BUILDSCRIPT,
                DEPENDENCY_CHECK_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(4);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandlePluginProperty() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("PluginProperty"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_PLUGIN_2_3_4_RELEASE);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).hasSize(29);
    }

    @Test
    public void shouldHandlePluginDefinedInSettingsFileWithApplyFalse() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("PluginDefinedInSettingsFileWithApplyFalse"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                MAVEN_CENTRAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT));
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_PLUGIN_2_3_4_RELEASE.withType(SoftwareType.GRADLE_PLUGIN_VERSION));
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandlePluginDefinedInSettingsFileWithApplyFalseAndAppliedInBuildFile() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("PluginDefinedInSettingsFileWithApplyFalseAndAppliedInBuildFile"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                MAVEN_CENTRAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_PLUGIN_2_3_4_RELEASE.withType(SoftwareType.GRADLE_PLUGIN_VERSION),
                SPRING_BOOT_PLUGIN_2_3_4_RELEASE);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).hasSize(29);
    }

    @Test
    public void shouldHandlePluginDefinedInSettingsFileWithApplyFalseAndDefinedAgainInBuildFile() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("PluginDefinedInSettingsFileWithApplyFalseAndAppliedInBuildFile"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                MAVEN_CENTRAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_PLUGIN_2_3_4_RELEASE.withType(SoftwareType.GRADLE_PLUGIN_VERSION),
                SPRING_BOOT_PLUGIN_2_3_4_RELEASE);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).hasSize(29);
    }

    @Test
    public void shouldHandlePluginDefinedInSettingsFileWithApplyTrue() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("PluginDefinedInSettingsFileWithApplyTrue"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                MAVEN_CENTRAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_PLUGIN_2_3_4_RELEASE);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).hasSize(29);
    }

    @Test
    public void shouldHandleSpringBootPlugin() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("SpringBootPlugin"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN,
                SPRING_BOOT_PLUGIN_2_3_4_RELEASE);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).hasSize(29);
    }

    @Test
    public void shouldHandleSpringBootPluginApplyPlugin() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("SpringBootPluginApplyPlugin"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                MAVEN_CENTRAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_GRADLE_PLUGIN_2_3_4_RELEASE,
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN,
                SPRING_BOOT_PLUGIN_2_3_4_RELEASE);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(10);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).hasSize(29);
    }

    @Test
    public void shouldHandleSpringBootPluginPluginsAndApplyPlugin() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("SpringBootPluginPluginsAndApplyPlugin"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN,
                SPRING_BOOT_PLUGIN_2_3_4_RELEASE);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).hasSize(29);
    }

    @Test
    public void shouldHandleGradleWrapper() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("GradleWrapper"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                GRADLE_WRAPPER_6_7,
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependencyNamedParts() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("DependencyNamedParts"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependencyNamedPartsWithClassifierAndExt() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("DependencyNamedPartsWithClassifierAndExt"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependencyMultipleInOneCall() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("DependencyMultipleInOneCall"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_ACTUATOR_2_3_4_RELEASE,
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(7);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependencyMultipleWithNamedPartsInOneCall() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("DependencyMultipleWithNamedPartsInOneCall"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_ACTUATOR_2_3_4_RELEASE,
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(7);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependencyStringConcatenation() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("DependencyStringConcatenation"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependencyProject() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("DependencyProject"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT));
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependencyLocalGroovy() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("DependencyLocalGroovy"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT));
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                GROOVY_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependencyGradleApi() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("DependencyGradleApi"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT));
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                GROOVY_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependencyFileTree() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("DependencyFileTree"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT));
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                GROOVY_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependencyFiles() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("DependencyFiles"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT));
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                GROOVY_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependencyVariable() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("DependencyVariable"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                HIBERNATE_VALIDATOR_6_1_6_FINAL,
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(28);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependencyVariableInDependencies() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("DependencyVariableInDependencies"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                HIBERNATE_VALIDATOR_6_1_6_FINAL,
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(28);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependencyDuplicates() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("DependencyDuplicates"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                LOMBOK_1_18_16);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleDependencyExclusion() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("DependencyExclusion"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_ACTUATOR_2_3_4_RELEASE,
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(7);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleProjectProperties() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("ProjectProperties"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                HIBERNATE_VALIDATOR_6_1_6_FINAL,
                LOMBOK_1_18_16,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(23);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleProjectPropertyAssignment() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("ProjectPropertyAssignment"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                HIBERNATE_VALIDATOR_6_1_6_FINAL,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(23);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleProjectPropertySetMethod() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("ProjectPropertySetMethod"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                HIBERNATE_VALIDATOR_6_1_6_FINAL,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(23);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleBuildscriptProjectProperty() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("BuildscriptProjectProperty"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                HIBERNATE_VALIDATOR_6_1_6_FINAL,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(23);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleBuildscriptProjectPropertyAssignment() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("BuildscriptProjectPropertyAssignment"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                MAVEN_CENTRAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT));
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_GRADLE_PLUGIN_2_3_4_RELEASE);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleBuildscriptDependencyWithNoBuildscriptRepository() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("BuildscriptDependencyWithNoBuildscriptRepository"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT));
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_GRADLE_PLUGIN_2_3_4_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleGradleProperties() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("GradleProperties"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                HIBERNATE_VALIDATOR_6_1_6_FINAL,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(23);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleGradlePropertiesAndProjectProperties() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("GradlePropertiesAndProjectProperties"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                HIBERNATE_VALIDATOR_4_1_0_FINAL,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(20);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleMultiProject() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("MultiProject"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                HIBERNATE_VALIDATOR_4_1_0_FINAL,
                LOMBOK_1_18_16,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(20);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleMultiProjectNestedGradleProperties() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("MultiProjectNestedGradleProperties"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                HIBERNATE_VALIDATOR_4_1_0_FINAL,
                LOMBOK_1_18_16,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(20);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleMultiProjectMissingBuildFile() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("MultiProjectMissingBuildFile"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                LOMBOK_1_18_16,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleMultiProjectSpringBootPlugin() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("MultiProjectSpringBootPlugin"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN,
                SPRING_BOOT_PLUGIN_2_3_4_RELEASE);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).hasSize(29);
    }

    @Test
    public void shouldHandleMultiProjectSpringBootPluginApplyFalse() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("MultiProjectSpringBootPluginApplyFalse"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN,
                SPRING_BOOT_PLUGIN_2_3_4_RELEASE.withType(SoftwareType.GRADLE_PLUGIN_VERSION),
                SPRING_BOOT_PLUGIN_2_3_4_RELEASE);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).hasSize(29);
    }

    @Test
    public void shouldHandleMultiProjectInheritedProjectProperties() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("MultiProjectInheritedProjectProperties"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                HIBERNATE_VALIDATOR_4_1_0_FINAL,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(20);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleMultiProjectOverriddenProjectProperties() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("MultiProjectOverriddenProjectProperties"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                HIBERNATE_VALIDATOR_4_1_0_FINAL,
                HIBERNATE_VALIDATOR_6_1_6_FINAL,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(36);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleMultiProjectAllprojects() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("MultiProjectAllprojects"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                HIBERNATE_VALIDATOR_4_1_0_FINAL,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(20);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleMultiProjectSubprojects() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("MultiProjectSubprojects"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                HIBERNATE_VALIDATOR_4_1_0_FINAL,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(20);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleMultiProjectSubprojectsRepositories() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("MultiProjectSubprojectsRepositories"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN,
                SPRING_BOOT_PLUGIN_2_3_4_RELEASE);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).hasSize(29);
    }

    @Test
    public void shouldHandlePlatformDependency() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("PlatformDependency"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                JAVA_PLUGIN,
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).hasSize(29);
    }

    @Test
    public void shouldHandleApplyFrom() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("ApplyFrom"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleApplyFromRootDir() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("ApplyFromRootDir"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleApplyFromRootProjectProjectDir() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("ApplyFromRootProjectProjectDir"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleApplyFromProjectDir() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("ApplyFromProjectDir"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleApplyFromProjectDirParent() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("ApplyFromProjectDirParent"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleApplyFromProjectRelativePath() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("ApplyFromProjectRelativePath"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(5);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleApplyFromTo() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("ApplyFromTo"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                MAVEN_CENTRAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT));
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleSpringDependencyManagementPlugin() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("SpringDependencyManagementPlugin"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_CLOUD_STARTER_ZIPKIN_2_2_5_RELEASE,
                SPRING_DEPENDENCY_MANAGEMENT_PLUGIN_1_0_10_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(7);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).hasSize(95);
    }

    @Test
    public void shouldHandleSpringDependencyManagementPluginStringConcatenation() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("SpringDependencyManagementPluginStringConcatenation"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GRADLE_PLUGIN_PORTAL_REPOSITORY.withScope(SoftwareRepositoryScope.BUILDSCRIPT),
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).containsExactlyInAnyOrder(
                SPRING_CLOUD_STARTER_ZIPKIN_2_2_5_RELEASE,
                SPRING_DEPENDENCY_MANAGEMENT_PLUGIN_1_0_10_RELEASE,
                JAVA_PLUGIN);
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).hasSize(7);
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).hasSize(95);
    }

    @Test
    public void shouldHandleImport() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("Import"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).isEmpty();
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleRepositoryMavenUrlMethodCall() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("RepositoryMavenUrlMethodCall"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GOOGLE_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleRepositoryMavenUrlMethodCallGString() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("RepositoryMavenUrlMethodCallGString"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GOOGLE_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleRepositoryMavenUrlProperty() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("RepositoryMavenUrlProperty"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GOOGLE_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleRepositoryMavenUrlPropertyGString() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("RepositoryMavenUrlPropertyGString"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GOOGLE_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleRepositoryMavenCentral() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("RepositoryMavenCentral"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                MAVEN_CENTRAL_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleRepositoryJCenter() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("RepositoryJCenter"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                JCENTER_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    @Test
    public void shouldHandleRepositoryGoogle() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("RepositoryGoogle"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        Component component = getMutatedComponent(output);
        assertThatGradleIsUsed(component);
        assertThat(getSoftwareRepositories(component)).containsExactlyInAnyOrder(
                GOOGLE_REPOSITORY);
        Map<SoftwareGroup, List<Software>> softwareGroups = getSoftwareGroups(component);
        assertThat(softwareGroups.get(SoftwareGroup.DIRECT)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.TRANSITIVE)).isNull();
        assertThat(softwareGroups.get(SoftwareGroup.BOM)).isNull();
    }

    private enum SoftwareGroup {

        BOM,
        DIRECT, TRANSITIVE

    }
}
