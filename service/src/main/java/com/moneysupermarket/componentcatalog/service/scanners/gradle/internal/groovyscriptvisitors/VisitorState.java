package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors;

import com.moneysupermarket.componentcatalog.sdk.models.Software;
import com.moneysupermarket.componentcatalog.sdk.models.SoftwareRepository;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.models.Import;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.utils.InheritingHashMap;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.utils.InheritingHashSet;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.With;

import java.nio.file.Path;
import java.util.Set;

@Value
@With
@RequiredArgsConstructor
@Builder
public class VisitorState {

    String scannerId;
    ProcessPhase processPhase;
    ProjectMode projectMode;
    Path codebaseDir;
    Path buildFile;
    Path applyFile;
    Set<Import> imports;
    InheritingHashSet<SoftwareRepository> buildscriptSoftwareRepositories;
    InheritingHashSet<SoftwareRepository> softwareRepositories;
    InheritingHashSet<Software> software;
    InheritingHashMap<String, String> properties;
    InheritingHashMap<String, Set<String>> dependencyVersions;
}
