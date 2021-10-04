package tech.kronicle.service.scanners.gradle.internal.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.kronicle.sdk.models.SoftwareDependencyType;
import tech.kronicle.sdk.models.SoftwareType;
import tech.kronicle.service.scanners.gradle.internal.constants.MavenPackagings;
import tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.VisitorState;

import java.util.Objects;

@Service
@Slf4j
public class BillOfMaterialsLogger {

     public void logManagedDependencies(VisitorState visitorState, Runnable action) {
         int directDependencyCount = getDirectDependencyCount(visitorState);
         int transitiveDependencyCount = getTransitiveDependencyCount(visitorState);
         int dependencyVersionCount = visitorState.getDependencyVersions().size();
         action.run();
         log.debug("Found {} direct bill of materials", getDirectDependencyCount(visitorState) - directDependencyCount);
         log.debug("Found {} transitive bill of materials", getTransitiveDependencyCount(visitorState) - transitiveDependencyCount);
         log.debug("Found {} dependency versions", visitorState.getDependencyVersions().size() - dependencyVersionCount);
     }


    private int getDirectDependencyCount(VisitorState visitorState) {
        return getDependencyCount(visitorState, SoftwareDependencyType.DIRECT);
    }

    private int getTransitiveDependencyCount(VisitorState visitorState) {
        return getDependencyCount(visitorState, SoftwareDependencyType.TRANSITIVE);
    }

    private int getDependencyCount(VisitorState visitorState, SoftwareDependencyType dependencyType) {
        return (int) visitorState.getSoftware().stream()
                .filter(software -> Objects.equals(software.getType(), SoftwareType.JVM)
                        && Objects.equals(software.getPackaging(), MavenPackagings.BOM)
                        && Objects.equals(software.getDependencyType(), dependencyType))
                .count();
    }
}
