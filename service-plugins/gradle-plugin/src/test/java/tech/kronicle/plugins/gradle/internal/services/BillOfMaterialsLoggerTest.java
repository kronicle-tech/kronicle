package tech.kronicle.plugins.gradle.internal.services;

import ch.qos.logback.classic.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.gradle.internal.constants.MavenPackagings;
import tech.kronicle.plugins.gradle.internal.groovyscriptvisitors.VisitorState;
import tech.kronicle.plugins.gradle.internal.utils.InheritingHashMap;
import tech.kronicle.plugins.gradle.internal.utils.InheritingHashSet;
import tech.kronicle.testutils.LogCaptor;
import tech.kronicle.testutils.SimplifiedLogEvent;
import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.SoftwareDependencyType;
import tech.kronicle.sdk.models.SoftwareType;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class BillOfMaterialsLoggerTest {

    private BillOfMaterialsLogger underTest;
    private LogCaptor logCaptor;

    @BeforeEach
    public void beforeEach() {
        underTest = new BillOfMaterialsLogger();
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
        software.add(createBomDependency(1, SoftwareDependencyType.DIRECT));
        software.add(createBomDependency(2, SoftwareDependencyType.TRANSITIVE));
        software.add(createBomDependency(3, SoftwareDependencyType.TRANSITIVE));
        InheritingHashMap<String, Set<String>> dependencyVersions = new InheritingHashMap<>();
        dependencyVersions.put("test-dependency-1", Set.of("test-version-1-1", "test-version-1-2"));
        VisitorState visitorState = VisitorState.builder()
                .software(software)
                .dependencyVersions(dependencyVersions)
                .build();

        // When
        underTest.logManagedDependencies(visitorState, () -> {
            software.add(createBomDependency(4, SoftwareDependencyType.DIRECT));
            software.add(createBomDependency(5, SoftwareDependencyType.DIRECT));
            software.add(createBomDependency(6, SoftwareDependencyType.TRANSITIVE));
            software.add(createBomDependency(7, SoftwareDependencyType.TRANSITIVE));
            software.add(createBomDependency(8, SoftwareDependencyType.TRANSITIVE));
            dependencyVersions.put("test-dependency-2", Set.of("test-version-2-1", "test-version-2-2"));
            dependencyVersions.put("test-dependency-3", Set.of("test-version-3-1", "test-version-3-2"));
            dependencyVersions.put("test-dependency-4", Set.of("test-version-4-1", "test-version-4-2"));
            dependencyVersions.put("test-dependency-5", Set.of("test-version-5-1", "test-version-5-2"));
        });

        // Then
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.DEBUG, "Found 2 direct bill of materials"),
                new SimplifiedLogEvent(Level.DEBUG, "Found 3 transitive bill of materials"),
                new SimplifiedLogEvent(Level.DEBUG, "Found 4 dependency versions"));
    }

    private Software createBomDependency(int number, SoftwareDependencyType dependencyType) {
        return Software.builder()
                .type(SoftwareType.JVM)
                .packaging(MavenPackagings.BOM)
                .dependencyType(dependencyType)
                .name("test-name-" + number)
                .version("test-version-" + number)
                .build();
    }
}
