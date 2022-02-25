package tech.kronicle.plugins.gradle.internal.testconstants;

import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.SoftwareDependencyType;
import tech.kronicle.sdk.models.SoftwareScope;
import tech.kronicle.sdk.models.SoftwareType;

import static tech.kronicle.plugins.gradle.internal.testconstants.ScannerIds.SCANNER_ID;

public final class SoftwareItems {
    
    public static final Software SPRING_CLOUD_STARTER_ZIPKIN_2_2_5_RELEASE = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.springframework.cloud:spring-cloud-starter-zipkin")
            .version("2.2.5.RELEASE")
            .build();
    public static final Software SPRING_BOOT_GRADLE_PLUGIN_2_3_4_RELEASE = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.springframework.boot:spring-boot-gradle-plugin")
            .version("2.3.4.RELEASE")
            .scope(SoftwareScope.BUILDSCRIPT)
            .build();
    public static final Software DEPENDENCY_CHECK_GRADLE_6_0_2_BUILDSCRIPT = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.owasp:dependency-check-gradle")
            .version("6.0.2")
            .scope(SoftwareScope.BUILDSCRIPT)
            .build();
    public static final Software GRADLE_WRAPPER_6_7 = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.TOOL)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("gradle-wrapper")
            .version("6.7")
            .build();
    public static final Software SPRING_BOOT_STARTER_ACTUATOR_2_3_4_RELEASE = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.springframework.boot:spring-boot-starter-actuator")
            .version("2.3.4.RELEASE")
            .build();
    public static final Software LOMBOK_1_18_16 = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.projectlombok:lombok")
            .version("1.18.16")
            .build();
    public static final Software SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.springframework.boot:spring-boot-starter-web")
            .version("2.3.4.RELEASE")
            .build();
    public static final Software SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE_SOURCES = SPRING_BOOT_STARTER_WEB_2_3_4_RELEASE
            .toBuilder()
            .packaging("sources")
            .build();
    public static final Software SPRING_BOOT_STARTER_WEB_2_0_9_RELEASE = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.springframework.boot:spring-boot-starter-web")
            .version("2.0.9.RELEASE")
            .build();
    public static final Software HIBERNATE_VALIDATOR_4_1_0_FINAL = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.hibernate.validator:hibernate-validator")
            .version("6.0.0.Final")
            .build();
    public static final Software HIBERNATE_VALIDATOR_6_1_6_FINAL = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.hibernate.validator:hibernate-validator")
            .version("6.1.6.Final")
            .build();
    public static final Software JTDS_1_3_1 = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("net.sourceforge.jtds:jtds")
            .version("1.3.1")
            .build();
    public static final Software JAVA_PLUGIN = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.GRADLE_PLUGIN)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("java")
            .build();
    public static final Software GROOVY_PLUGIN = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.GRADLE_PLUGIN)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("groovy")
            .build();
    public static final Software SPRING_DEPENDENCY_MANAGEMENT_PLUGIN_1_0_10_RELEASE = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.GRADLE_PLUGIN)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("io.spring.dependency-management")
            .version("1.0.10.RELEASE")
            .build();
    public static final Software SPRING_BOOT_PLUGIN_2_3_4_RELEASE = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.GRADLE_PLUGIN)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.springframework.boot")
            .version("2.3.4.RELEASE")
            .build();
    public static final Software DEPENDENCY_CHECK_PLUGIN = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.GRADLE_PLUGIN)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("org.owasp.dependencycheck.gradle.DependencyCheckPlugin")
            .build();
    public static final Software MICRONAUT_APPLICATION_PLUGIN_2_0_6 = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.GRADLE_PLUGIN)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("io.micronaut.application")
            .version("2.0.6")
            .build();
    public static final Software MICRONAUT_LIBRARY_PLUGIN_2_0_6 = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.GRADLE_PLUGIN)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("io.micronaut.library")
            .version("2.0.6")
            .build();
    public static final Software MICRONAUT_RUNTIME_3_0_0 = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("io.micronaut:micronaut-runtime")
            .version("3.0.0")
            .build();
    public static final Software MICRONAUT_RUNTIME_3_1_0 = Software
            .builder()
            .scannerId(SCANNER_ID)
            .type(SoftwareType.JVM)
            .dependencyType(SoftwareDependencyType.DIRECT)
            .name("io.micronaut:micronaut-runtime")
            .version("3.1.0")
            .build();

    private SoftwareItems() {
    }
}
