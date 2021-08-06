package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors;

public enum ProcessPhase {

    INITIALIZE,
    PROPERTIES,
    PLUGINS,
    BUILDSCRIPT_REPOSITORIES,
    BUILDSCRIPT_DEPENDENCIES,
    APPLY_PLUGINS,
    REPOSITORIES,
    DEPENDENCY_MANAGEMENT,
    DEPENDENCIES,
    FINALIZE
}
