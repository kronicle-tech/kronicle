package tech.kronicle.service.scanners.nodejs.internal.services.npm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.SoftwareDependencyType;
import tech.kronicle.sdk.models.SoftwareScope;
import tech.kronicle.service.scanners.nodejs.internal.constants.NodeJsFileNames;
import tech.kronicle.service.scanners.nodejs.internal.constants.NpmPackagings;
import tech.kronicle.service.scanners.nodejs.internal.models.PackageJson;
import tech.kronicle.service.scanners.nodejs.internal.models.npm.NpmDependencies;
import tech.kronicle.service.scanners.nodejs.internal.models.npm.NpmDependency;
import tech.kronicle.service.scanners.nodejs.internal.models.npm.NpmPackageLock;
import tech.kronicle.service.utils.FileUtils;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class NpmPackageExtractor {

    private final FileUtils fileUtils;
    private final ObjectMapper objectMapper;

    public Stream<Software> extractPackages(Path file) {
        PackageJson packageJson = readPackageJsonFile(getPackageJsonFile(file));
        if (isNull(packageJson)) {
            return Stream.empty();
        }
        NpmPackageLock npmPackageLock = readNpmPackageLockFile(file);
        return extractSoftware(npmPackageLock, getDirectDependencyNames(packageJson), SoftwareDependencyType.DIRECT);
    }

    private Path getPackageJsonFile(Path packageLock) {
        return packageLock.resolveSibling(NodeJsFileNames.PACKAGE_JSON);
    }

    private PackageJson readPackageJsonFile(Path file) {
        if (!fileUtils.fileExists(file)) {
            return null;
        }
        return readJsonFile(file, PackageJson.class);
    }

    private NpmPackageLock readNpmPackageLockFile(Path file) {
        return readJsonFile(file, NpmPackageLock.class);
    }

    private <T> T readJsonFile(Path file, Class<T> valueType) {
        try {
            return objectMapper.readValue(fileUtils.readFileContent(file), valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<String> getDirectDependencyNames(PackageJson packageJson) {
        return Stream.of(packageJson.getDependencies(), packageJson.getDevDependencies())
                .filter(it -> nonNull(it))
                .flatMap(it -> it.keySet().stream())
                .collect(Collectors.toSet());
    }

    private Stream<Software> extractSoftware(NpmDependencies npmDependencies, Set<String> directDependencyNames, SoftwareDependencyType dependencyType) {
        if (isNull(npmDependencies.getDependencies())) {
            return Stream.empty();
        }
        return npmDependencies.getDependencies().entrySet().stream()
                .flatMap(entry -> Stream.concat(
                        Stream.of(createSoftwareItem(entry, directDependencyNames, dependencyType)),
                        extractSoftware(entry.getValue(), directDependencyNames, SoftwareDependencyType.TRANSITIVE)));
    }

    private Software createSoftwareItem(Map.Entry<String, NpmDependency> entry, Set<String> directDependencyNames, SoftwareDependencyType dependencyType) {
        return Software.builder()
                .name(entry.getKey())
                .packaging(NpmPackagings.NPM_PACKAGE)
                .version(entry.getValue().getVersion())
                .dependencyType(getDependencyType(entry.getKey(), directDependencyNames, dependencyType))
                .scope(getScope(entry.getValue().getDev()))
                .build();
    }

    private SoftwareDependencyType getDependencyType(String name, Set<String> directDependencyNames, SoftwareDependencyType dependencyType) {
        return dependencyType == SoftwareDependencyType.DIRECT && directDependencyNames.contains(name)
                ? SoftwareDependencyType.DIRECT
                : SoftwareDependencyType.TRANSITIVE;
    }

    private SoftwareScope getScope(Boolean dev) {
        return nonNull(dev) && dev ? SoftwareScope.DEV : null;
    }
}
