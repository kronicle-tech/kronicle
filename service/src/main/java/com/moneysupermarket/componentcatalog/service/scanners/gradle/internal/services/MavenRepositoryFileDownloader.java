package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services;

import com.moneysupermarket.componentcatalog.sdk.models.SoftwareRepository;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.utils.ArtifactUtils;
import com.moneysupermarket.componentcatalog.service.services.Downloader;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MavenRepositoryFileDownloader {

    private static final int MAX_REDIRECT_COUNT = 1;
    private final ArtifactUtils artifactUtils;
    private final Downloader downloader;

    public MavenFileRequestOutcome<String> downloadArtifact(String artifactCoordinates, String packaging, Set<SoftwareRepository> softwareRepositories) {
        return downloadMavenRepositoryFile(getArtifactPath(artifactCoordinates, packaging), softwareRepositories);
    }

    public MavenFileRequestOutcome<Boolean> checkArtifactExists(String artifactCoordinates, String packaging, Set<SoftwareRepository> softwareRepositories) {
        return checkMavenRepositoryFileExists(getArtifactPath(artifactCoordinates, packaging), softwareRepositories);
    }

    public MavenFileRequestOutcome<String> downloadMetadata(String groupId, String artifactId, Set<SoftwareRepository> softwareRepositories) {
        return downloadMavenRepositoryFile(getMetadataPath(groupId, artifactId), softwareRepositories);
    }

    private MavenFileRequestOutcome<String> downloadMavenRepositoryFile(String filePath, Set<SoftwareRepository> softwareRepositories) {
        return makeMavenRepositoryFileRequest(filePath, softwareRepositories, url -> downloader.download(url, MAX_REDIRECT_COUNT));
    }

    private MavenFileRequestOutcome<Boolean> checkMavenRepositoryFileExists(String filePath, Set<SoftwareRepository> softwareRepositories) {
        return makeMavenRepositoryFileRequest(filePath, softwareRepositories, url -> downloader.exists(url, MAX_REDIRECT_COUNT));
    }

    private <T> MavenFileRequestOutcome<T> makeMavenRepositoryFileRequest(String filePath, Set<SoftwareRepository> softwareRepositories,
            Function<String, Downloader.HttpRequestOutcome<T>> downloadMaker) {
        List<String> safeRepositoryUrls = getSafeSoftwareRepositoryUrls(softwareRepositories);
        List<Exception> exceptions = new ArrayList<>();

        for (String repositoryUrl : safeRepositoryUrls) {
            Downloader.HttpRequestOutcome<T> output = downloadMaker.apply(repositoryUrl + filePath);

            if (output.isSuccess()) {
                return MavenFileRequestOutcome.from(output);
            }

            exceptions.addAll(output.getExceptions());
        }

        return new MavenFileRequestOutcome<>(null, false, null, exceptions);
    }

    private List<String> getSafeSoftwareRepositoryUrls(Set<SoftwareRepository> softwareRepositories) {
        List<String> list = softwareRepositories.stream()
                .filter(SoftwareRepository::getSafe)
                .map(softwareRepository -> softwareRepository.getUrl().replaceAll("/+$", ""))
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            throw new IllegalArgumentException("No safe repositories configured");
        }

        return list;
    }

    private String getArtifactPath(String artifact, String packaging) {
        ArtifactUtils.ArtifactParts artifactParts = artifactUtils.getArtifactParts(artifact);
        return getArtifactPath(artifactParts.getGroupId(), artifactParts.getArtifactId(), artifactParts.getVersion(), packaging);
    }

    private String getArtifactPath(String groupId, String artifactId, String version, String packaging) {
        return getGroupIdAndArtifactIdPath(groupId, artifactId)
                + version + "/"
                + artifactId + "-" + version + "." + packaging;
    }

    private String getMetadataPath(String groupId, String artifactId) {
        return getGroupIdAndArtifactIdPath(groupId, artifactId)
                + "maven-metadata.xml";
    }

    private String getGroupIdAndArtifactIdPath(String groupId, String artifactId) {
        return getGroupIdPath(groupId) + "/" + artifactId + "/";
    }

    private String getGroupIdPath(String groupId) {
        return "/" + groupId.replaceAll("\\.", "/");
    }

    @Value
    public static class MavenFileRequestOutcome<T> {

        String url;
        boolean success;
        T output;
        List<Exception> exceptions;

        public boolean isFailure() {
            return !success;
        }

        public static <T> MavenFileRequestOutcome<T> from(Downloader.HttpRequestOutcome<T> value) {
            return new MavenFileRequestOutcome<>(value.getUrl(), value.isSuccess(), value.getOutput(), value.getExceptions());
        }
    }
}
