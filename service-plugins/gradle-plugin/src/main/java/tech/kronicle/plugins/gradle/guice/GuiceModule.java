package tech.kronicle.plugins.gradle.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import tech.kronicle.plugins.gradle.GradleStaticAnalyzer;
import tech.kronicle.plugins.gradle.config.GradleConfig;

import static tech.kronicle.plugins.gradle.GradleStaticAnalyzerFactory.newGradleStaticAnalyzer;

public class GuiceModule extends AbstractModule {

    @Provides
    public GradleStaticAnalyzer gradleStaticAnalyzer(GradleConfig config) {
        return newGradleStaticAnalyzer(config);
    }
}
