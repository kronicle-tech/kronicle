package tech.kronicle.plugins.gradle.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.SoftwareDependencyType;
import tech.kronicle.sdk.models.SoftwareType;
import tech.kronicle.utils.FileUtils;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;
import static tech.kronicle.common.StringEscapeUtils.escapeString;
import static tech.kronicle.plugins.gradle.constants.GradleFileNames.GRADLE_WRAPPER_PROPERTIES;
import static tech.kronicle.plugins.gradle.constants.GradleWrapperPropertyNames.DISTRIBUTION_URL;
import static tech.kronicle.plugins.gradle.constants.ToolNames.GRADLE_WRAPPER;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class GradleWrapperFinder {

    private static final Pattern GRADLE_WRAPPER_VERSION_EXTRACTION_PATTERN = Pattern.compile("/gradle-(\\d+\\.\\d+(\\.\\d+)?)-");

    private final FileUtils fileUtils;

    public Software findGradleWrapper(Path dir) {
        Path gradleWrapperPropertiesFile = dir.resolve("gradle").resolve("wrapper").resolve(GRADLE_WRAPPER_PROPERTIES);

        if (fileUtils.fileExists(gradleWrapperPropertiesFile)) {
            Properties gradleWrapperProperties = fileUtils.loadProperties(gradleWrapperPropertiesFile);
            String distributionUrl = gradleWrapperProperties.getProperty(DISTRIBUTION_URL);
            if (isNull(distributionUrl)) {
                log.warn(GRADLE_WRAPPER_PROPERTIES + " file does not contain a \"" + DISTRIBUTION_URL +  "\" property");
                return null;
            } else {
                return Software.builder()
                        .type(SoftwareType.TOOL)
                        .dependencyType(SoftwareDependencyType.DIRECT)
                        .name(GRADLE_WRAPPER)
                        .version(extractGradleWrapperVersionFromDistributionUrl(distributionUrl))
                        .build();
            }
        } else {
            return null;
        }
    }

    private String extractGradleWrapperVersionFromDistributionUrl(String distributionUrl) {
        Matcher matcher = GRADLE_WRAPPER_VERSION_EXTRACTION_PATTERN.matcher(distributionUrl);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Could not extract Gradle Wrapper version from distribution URL \"" + escapeString(distributionUrl) + "\"");
        }

        return matcher.group(1);
    }
}
