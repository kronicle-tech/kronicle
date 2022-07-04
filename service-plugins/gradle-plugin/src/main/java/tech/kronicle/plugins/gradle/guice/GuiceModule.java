package tech.kronicle.plugins.gradle.guice;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import tech.kronicle.gradlestaticanalyzer.GradleStaticAnalyzer;
import tech.kronicle.gradlestaticanalyzer.config.GradleStaticAnalyzerConfig;
import tech.kronicle.utils.FileUtils;

import static tech.kronicle.gradlestaticanalyzer.GradleStaticAnalyzerFactory.newGradleStaticAnalyzer;
import static tech.kronicle.utils.FileUtilsFactory.createFileUtils;

public class GuiceModule extends AbstractModule {

    @Provides
    public FileUtils fileUtils() {
        return createFileUtils();
    }

    @Provides
    public YAMLMapper yamlMapper() {
        return new YAMLMapper();
    }

    @Provides
    public GradleStaticAnalyzer gradleStaticAnalyzer(GradleStaticAnalyzerConfig config) {
        return newGradleStaticAnalyzer(config);
    }
}
