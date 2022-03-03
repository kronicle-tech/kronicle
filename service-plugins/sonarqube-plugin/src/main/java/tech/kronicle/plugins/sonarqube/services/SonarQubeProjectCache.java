package tech.kronicle.plugins.sonarqube.services;

import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SonarQubeProjectCache {

    private final Map<Path, List<SonarQubeProject>> innerCache = new HashMap<>();

    public List<SonarQubeProject> get(Path codebaseDir) {
        return innerCache.get(codebaseDir);
    }

    public void put(Path codebaseDir, List<SonarQubeProject> sonarQubeProjects) {
        innerCache.put(codebaseDir, sonarQubeProjects);
    }

    public void clear() {
        innerCache.clear();
    }

    public Set<String> getUsedProjectKeys() {
        return innerCache.values().stream()
                .flatMap(Collection::stream)
                .map(SonarQubeProject::getKey)
                .collect(Collectors.toSet());
    }
}
