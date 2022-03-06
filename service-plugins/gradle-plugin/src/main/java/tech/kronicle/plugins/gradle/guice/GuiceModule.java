package tech.kronicle.plugins.gradle.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import tech.kronicle.gradlestaticanalyzer.GradleStaticAnalyzer;
import tech.kronicle.gradlestaticanalyzer.config.GradleStaticAnalyzerConfig;

import static tech.kronicle.gradlestaticanalyzer.GradleStaticAnalyzerFactory.newGradleStaticAnalyzer;

public class GuiceModule extends AbstractModule {

    @Provides
    public GradleStaticAnalyzer gradleStaticAnalyzer(GradleStaticAnalyzerConfig config) {
        return newGradleStaticAnalyzer(config);
    }
}
