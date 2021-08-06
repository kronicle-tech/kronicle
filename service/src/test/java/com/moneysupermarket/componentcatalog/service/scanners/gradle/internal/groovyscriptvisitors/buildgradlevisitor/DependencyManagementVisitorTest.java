package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors.buildgradlevisitor;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.moneysupermarket.componentcatalog.sdk.models.Software;
import com.moneysupermarket.componentcatalog.sdk.models.SoftwareRepository;
import com.moneysupermarket.componentcatalog.sdk.models.SoftwareRepositoryType;
import com.moneysupermarket.componentcatalog.service.config.DownloadCacheConfig;
import com.moneysupermarket.componentcatalog.service.config.UrlExistsCacheConfig;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.GradleScannerTestConfiguration;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.config.GradleConfig;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.constants.SoftwareRepositoryUrls;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors.GroovyParser;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors.ProcessPhase;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors.VisitorState;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.utils.InheritingHashMap;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.utils.InheritingHashSet;
import com.moneysupermarket.componentcatalog.service.testutils.LogCaptor;
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

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(properties = {
        "download-cache.dir=build/test-data/com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors.buildgradlevisitor.DependencyManagementVisitorTest/download-cache", 
        "url-exists-cache.dir=build/test-data/com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors.buildgradlevisitor.DependencyManagementVisitorTest/url-exists-cache", 
        "gradle.pom-cache-dir=build/test-data/com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors.buildgradlevisitor.DependencyManagementVisitorTest/gradle/pom-cache"
})
@ContextConfiguration(classes = GradleScannerTestConfiguration.class)
@EnableConfigurationProperties(value = {DownloadCacheConfig.class, UrlExistsCacheConfig.class, GradleConfig.class})
public class DependencyManagementVisitorTest {

    @Autowired
    private DependencyManagementVisitor underTest;
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
    public void getDependencyCountShouldHandleNullsInExistingSoftware() {
        // Given
        InheritingHashSet<Software> software = new InheritingHashSet<>();
        software.add(Software.builder().build());
        InheritingHashMap<String, Set<String>> dependencyVersions = new InheritingHashMap<>();
        InheritingHashSet<SoftwareRepository> softwareRepositories = new InheritingHashSet<>();
        softwareRepositories.add(SoftwareRepository.builder()
                .type(SoftwareRepositoryType.MAVEN)
                .url(SoftwareRepositoryUrls.MAVEN_CENTRAL)
                .safe(true)
                .build());
        VisitorState visitorState = VisitorState.builder()
                .processPhase(ProcessPhase.DEPENDENCY_MANAGEMENT)
                .softwareRepositories(softwareRepositories)
                .software(software)
                .properties(new InheritingHashMap<>())
                .dependencyVersions(dependencyVersions)
                .build();
        underTest.setVisitorState(visitorState, null);
        List<ASTNode> nodes = groovyParser.parseGroovy(
                        "imports {\n"
                        + "  mavenBom \"org.springframework.cloud:spring-cloud-dependencies:Hoxton.SR8\"\n"
                        + "}\n");

        // When
        nodes.forEach(node -> node.visit(underTest));

        // Then
        assertThat(software).hasSize(96);

        List<ILoggingEvent> events = logCaptor.getEvents();
        assertThat(events).hasSize(4);
        ILoggingEvent event;
        event = events.get(0);
        assertThat(event.getFormattedMessage()).isEqualTo("Found imports");
        event = events.get(1);
        assertThat(event.getFormattedMessage()).isEqualTo("Found 1 direct bill of materials");
        event = events.get(2);
        assertThat(event.getFormattedMessage()).isEqualTo("Found 94 transitive bill of materials");
        event = events.get(3);
        assertThat(event.getFormattedMessage()).isEqualTo("Found 865 dependency versions");
    }
}
