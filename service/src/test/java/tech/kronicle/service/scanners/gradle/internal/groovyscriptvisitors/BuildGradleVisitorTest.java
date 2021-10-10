package tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.codehaus.groovy.ast.ASTNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.SoftwareType;
import tech.kronicle.service.config.DownloadCacheConfig;
import tech.kronicle.service.config.UrlExistsCacheConfig;
import tech.kronicle.service.constants.Comparators;
import tech.kronicle.service.scanners.gradle.GradleScannerTestConfiguration;
import tech.kronicle.service.scanners.gradle.config.GradleConfig;
import tech.kronicle.service.scanners.gradle.internal.utils.InheritingHashMap;
import tech.kronicle.service.scanners.gradle.internal.utils.InheritingHashSet;
import tech.kronicle.service.testutils.LogCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "download-cache.dir=build/test-data/tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.BuildGradleVisitorTest/download-cache", 
        "url-exists-cache.dir=build/test-data/tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.BuildGradleVisitorTest/url-exists-cache", 
        "gradle.pom-cache-dir=build/test-data/tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.BuildGradleVisitorTest/gradle/pom-cache"
})
@ContextConfiguration(classes = GradleScannerTestConfiguration.class)
@EnableConfigurationProperties(value = {DownloadCacheConfig.class, UrlExistsCacheConfig.class, GradleConfig.class})
@ExtendWith(MockitoExtension.class)
public class BuildGradleVisitorTest {

    @Autowired
    private BuildGradleVisitor underTest;
    @Autowired
    private GroovyParser groovyParser;
    private LogCaptor logCaptor;

    @BeforeEach
    public void beforeEach() {
        logCaptor = new LogCaptor(underTest.getClass());
    }

    @AfterEach
    public void afterEach() {
        logCaptor.close();
    }

    @Test
    public void getPluginCountShouldHandleNullsInExistingSoftware() {
        // Given
        InheritingHashSet<Software> softwareSet = new InheritingHashSet<>();
        softwareSet.add(new Software(null, null, null, null, null, null, null, null));
        VisitorState visitorState = new VisitorState(null, ProcessPhase.PLUGINS, null, null, null, null, null, new InheritingHashSet<>(), null, softwareSet, new InheritingHashMap<>(), null);
        underTest.setVisitorState(visitorState, null);
        List<ASTNode> nodes = groovyParser.parseGroovy(
                "plugins {\n"
                        + "  id \"test\"\n"
                        + "}\n");

        // When
        nodes.forEach(node -> node.visit(underTest));

        // Then
        List<Software> softwareList = getSoftware(softwareSet);
        assertThat(softwareList).hasSize(2);
        Software software;
        software = softwareList.get(0);
        assertThat(software.getType()).isEqualTo(SoftwareType.GRADLE_PLUGIN);
        assertThat(software.getName()).isEqualTo("test");
        assertThat(software.getVersion()).isNull();
        assertThat(software.getPackaging()).isNull();
        assertThat(software.getScope()).isNull();
        software = softwareList.get(1);
        assertThat(software.getType()).isNull();
        assertThat(software.getName()).isNull();
        assertThat(software.getVersion()).isNull();
        assertThat(software.getPackaging()).isNull();
        assertThat(software.getScope()).isNull();

        List<ILoggingEvent> events = logCaptor.getEvents();
        assertThat(events).hasSize(2);
        ILoggingEvent event;
        event = events.get(0);
        assertThat(event.getFormattedMessage()).isEqualTo("Found plugins");
        event = events.get(1);
        assertThat(event.getFormattedMessage()).isEqualTo("Found 1 plugins");
    }

    private List<Software> getSoftware(InheritingHashSet<Software> software) {
        ArrayList<Software> list = new ArrayList<>(software);
        list.sort(Comparators.SOFTWARE);
        return list;
    }
}
