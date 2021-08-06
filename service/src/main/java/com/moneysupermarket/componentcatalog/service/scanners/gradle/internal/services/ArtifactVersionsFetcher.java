package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services;

import com.moneysupermarket.componentcatalog.sdk.models.SoftwareRepository;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.models.mavenxml.Metadata;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.models.mavenxml.metadata.Versioning;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.models.mavenxml.metadata.Versions;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.utils.ArtifactUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.moneysupermarket.componentcatalog.common.utils.StringEscapeUtils.escapeString;

@Service
@RequiredArgsConstructor
public class ArtifactVersionsFetcher {

    private final MavenRepositoryFileDownloader mavenRepositoryFileDownloader;
    private final ArtifactUtils artifactUtils;
    private Unmarshaller unmarshaller;
    private XMLInputFactory xmlInputFactory;

    @PostConstruct
    public void initialize() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Metadata.class);
        unmarshaller = context.createUnmarshaller();
        xmlInputFactory = XMLInputFactory.newFactory();
        xmlInputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
    }

    public List<String> fetchArtifactVersions(String groupId, String artifactId, Set<SoftwareRepository> softwareRepositories) {
        MavenRepositoryFileDownloader.MavenFileRequestOutcome<String> xmlContent =
                mavenRepositoryFileDownloader.downloadMetadata(groupId, artifactId, softwareRepositories);

        if (xmlContent.isFailure()) {
            throw new IllegalArgumentException("Could not retrieve maven metadata file for artifact coordinates "
                    + "\"" + escapeString(artifactUtils.createName(groupId, artifactId))
                    + "\" from safe subset of configured repositories");
        }

        Metadata metadata = readMetadataXml(xmlContent.getOutput());
        return Optional.of(metadata).map(Metadata::getVersioning).map(Versioning::getVersions).map(Versions::getVersions)
                .orElseGet(List::of);
    }

    private Metadata readMetadataXml(String content) {
        try {
            return (Metadata) unmarshaller.unmarshal(xmlInputFactory.createXMLStreamReader(new StringReader(content)));
        } catch (JAXBException | XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
}
