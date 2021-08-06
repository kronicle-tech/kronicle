package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors.buildgradlevisitor;

import com.moneysupermarket.componentcatalog.sdk.models.Software;
import com.moneysupermarket.componentcatalog.sdk.models.SoftwareDependencyType;
import com.moneysupermarket.componentcatalog.sdk.models.SoftwareRepository;
import com.moneysupermarket.componentcatalog.sdk.models.SoftwareType;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.DependencyVersionFetcher;

import java.util.Map;
import java.util.Set;

public class FakeDependencyVersionFetcher extends DependencyVersionFetcher {

    public FakeDependencyVersionFetcher() {
        super(null, null);
    }

    @Override
    public void findDependencyVersions(String scannerId, String pomArtifactCoordinates, Set<SoftwareRepository> softwareRepositories,
            Map<String, Set<String>> dependencyVersions, Set<Software> software) {
        software.add(new Software("test_scanner", SoftwareType.JVM, SoftwareDependencyType.DIRECT, "test_name", "test_version",
                null, "bom", null));
        software.add(new Software("test_scanner", SoftwareType.JVM, SoftwareDependencyType.TRANSITIVE, "test_name", "test_version",
                null, "bom", null));
        dependencyVersions.put("test_name", Set.of("test_version"));
    }
}
