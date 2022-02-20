package tech.kronicle.plugins.gradle.internal.constants;

import java.util.List;

public final class SoftwareRepositoryUrls {

    public static final String GOOGLE = "https://dl.google.com/dl/android/maven2/";
    public static final String GRADLE_PLUGIN_PORTAL = "https://plugins.gradle.org/m2/";
    public static final String JCENTER = "https://jcenter.bintray.com/";
    public static final String MAVEN_CENTRAL = "https://repo.maven.apache.org/maven2/";
    public static final String SPRING_PLUGINS_RELEASE = "https://repo.spring.io/plugins-release";
    public static final List<String> SAFE_REPOSITORY_URLS = List.of(GRADLE_PLUGIN_PORTAL, JCENTER, MAVEN_CENTRAL, SPRING_PLUGINS_RELEASE);

    private SoftwareRepositoryUrls() {
    }
}
