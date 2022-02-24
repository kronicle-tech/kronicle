package tech.kronicle.plugins.gradle.internal.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Component;
import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.SoftwareDependencyType;
import tech.kronicle.sdk.models.SoftwareRepository;
import tech.kronicle.sdk.models.SoftwareType;
import tech.kronicle.plugins.gradle.internal.models.Pom;
import tech.kronicle.plugins.gradle.internal.models.PomOutcome;
import tech.kronicle.plugins.gradle.internal.models.mavenxml.DependenciesContainer;
import tech.kronicle.plugins.gradle.internal.models.mavenxml.Project;
import tech.kronicle.plugins.gradle.internal.models.mavenxml.ProjectCoordinates;
import tech.kronicle.plugins.gradle.internal.models.mavenxml.project.Dependencies;
import tech.kronicle.plugins.gradle.internal.models.mavenxml.project.Dependency;
import tech.kronicle.plugins.gradle.internal.models.mavenxml.project.Parent;
import tech.kronicle.plugins.gradle.internal.models.mavenxml.project.Properties;
import tech.kronicle.plugins.gradle.internal.utils.ArtifactUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.common.utils.StringEscapeUtils;
import tech.kronicle.pluginutils.utils.StringUtils;

import javax.annotation.PostConstruct;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static tech.kronicle.plugins.gradle.internal.constants.MavenPackagings.JAR;
import static tech.kronicle.plugins.gradle.internal.constants.MavenPackagings.POM;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class PomFetcher {

    private final MavenRepositoryFileDownloader mavenRepositoryFileDownloader;
    private final PomCache pomCache;
    private final PropertyExpander propertyExpander;
    private final ObjectMapper objectMapper;
    private final ArtifactUtils artifactUtils;
    private Unmarshaller unmarshaller;
    private XMLInputFactory xmlInputFactory;

    @PostConstruct
    public void initialize() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Project.class);
        unmarshaller = context.createUnmarshaller();
        xmlInputFactory = XMLInputFactory.newFactory();
        xmlInputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
    }

    public PomOutcome fetchPom(String pomArtifactCoordinates, Set<SoftwareRepository> softwareRepositories) {
        log.debug("Processing POM \"" + StringEscapeUtils.escapeString(pomArtifactCoordinates) + "\"");

        log.debug("Downloading and processing POM");
        MavenRepositoryFileDownloader.MavenFileRequestOutcome<String> xmlContent = mavenRepositoryFileDownloader.downloadArtifact(pomArtifactCoordinates, POM, softwareRepositories);

        if (xmlContent.isFailure()) {
            if (!xmlContent.getExceptions().isEmpty()) {
                throw new IllegalArgumentException("Error with retrieving POM artifact \"" + StringEscapeUtils.escapeString(pomArtifactCoordinates) + "\"",
                        getFirstException(xmlContent.getExceptions()));
            }

            MavenRepositoryFileDownloader.MavenFileRequestOutcome<Boolean> exists = mavenRepositoryFileDownloader.checkArtifactExists(pomArtifactCoordinates, JAR, softwareRepositories);

            if (exists.isFailure()) {
                throw new IllegalArgumentException("Error with checking existence of JAR artifact \"" + StringEscapeUtils.escapeString(pomArtifactCoordinates) + "\"",
                        getFirstException(exists.getExceptions()));
            } else if (exists.getOutput()) {
                return PomOutcome.builder().jarOnly(true).build();
            } else {
                throw new IllegalArgumentException("Could not retrieve POM artifact \"" + StringEscapeUtils.escapeString(pomArtifactCoordinates)
                        + "\" from safe subset of configured repositories");
            }
        }

        Optional<String> jsonContent = pomCache.get(xmlContent.getUrl());
        Pom pom;

        if (jsonContent.isPresent()) {
            log.debug("Found cached copy of POM");
            pom = readPomJson(jsonContent.get());
        } else {
            Project project = readPomXml(xmlContent.getOutput());
            pom = fetchPom(pomArtifactCoordinates, project, softwareRepositories);
            jsonContent = Optional.of(writePomJson(pom));
            pomCache.put(xmlContent.getUrl(), jsonContent.get());
        }

        log.debug("Found {} software items", nonNull(pom.getDependencyManagementDependencies()) ? pom.getDependencyManagementDependencies().size() : 0);

        return PomOutcome.builder().jarOnly(false).pom(pom).build();
    }

    private Exception getFirstException(List<Exception> exceptions) {
        return exceptions.isEmpty() ? null : exceptions.get(0);
    }

    protected Pom readPomJson(String content) {
        try {
            return objectMapper.readValue(content, Pom.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not read POM JSON", e);
        }
    }

    private String writePomJson(Pom pom) {
        try {
            return objectMapper.writeValueAsString(pom);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not write POM JSON", e);
        }
    }

    private Project readPomXml(String content) {
        try {
            return (Project) unmarshaller.unmarshal(xmlInputFactory.createXMLStreamReader(new StringReader(content)));
        } catch (JAXBException | XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private Pom fetchPom(String pomArtifactCoordinates, Project project, Set<SoftwareRepository> softwareRepositories) {
        Pom.PomBuilder pomBuilder = Pom.builder()
                .artifactCoordinates(pomArtifactCoordinates);
        Map<String, String> properties = new HashMap<>();
        Parent parent = project.getParent();

        if (nonNull(parent)) {
            String parentArtifactCoordinates = artifactUtils.createArtifact(parent.getGroupId(), parent.getArtifactId(), parent.getVersion());
            Optional<Pom> parentPom = processRelatedPom(pomBuilder, parentArtifactCoordinates, softwareRepositories);

            if (parentPom.isEmpty()) {
                throw new IllegalArgumentException("Could not retrieve parent POM "
                        + "\"" + StringEscapeUtils.escapeString(parentArtifactCoordinates)
                        + "\" from safe subset of configured repositories");
            }


            properties.putAll(parentPom.get().getProperties());
        }

        addProperties(properties, project);
        pomBuilder.properties(properties);
        Pom temporaryPom = pomBuilder.build();

        processDependencies(
                Optional.of(project).map(Project::getDependencyManagement),
                DependencyType.DEPENDENCY_MANAGEMENT,
                softwareRepositories,
                temporaryPom,
                pomBuilder);
        temporaryPom = pomBuilder.build();
        processDependencies(
                Optional.of(project),
                DependencyType.NORMAL,
                softwareRepositories,
                temporaryPom,
                pomBuilder);

        return pomBuilder.build();
    }

    private void processDependencies(Optional<DependenciesContainer> dependenciesContainer, DependencyType dependencyType,
                                     Set<SoftwareRepository> softwareRepositories, Pom pom, Pom.PomBuilder pomBuilder) {
        dependenciesContainer.map(DependenciesContainer::getDependencies)
                .map(Dependencies::getDependencies)
                .ifPresent(dependencies -> dependencies.forEach(dependency ->
                                processDependency(dependency, dependencyType, softwareRepositories, pom, pomBuilder)));
    }

    private void processDependency(Dependency dependency, DependencyType dependencyType, Set<SoftwareRepository> softwareRepositories,
            Pom pom, Pom.PomBuilder pomBuilder) {
        String groupId = getNonEmptyElementWithPropertyExpansion("groupId", dependency.getGroupId(), pom);
        String artifactId = getNonEmptyElementWithPropertyExpansion("artifactId", dependency.getArtifactId(), pom);
        String type = requireNoPropertyReferences("type", dependency.getType());
        String scope = requireNoPropertyReferences("scope", dependency.getScope());
        List<String> versions;

        if (nonNull(dependency.getVersion()) && !dependency.getVersion().isEmpty()) {
            String version = getNonEmptyElementWithPropertyExpansion("version", dependency.getVersion(), pom);
            versions = List.of(version);
        } else {
            String name = artifactUtils.createName(groupId, artifactId);
            versions = pom.getDependencyManagementDependencies().stream()
                    .filter(item -> Objects.equals(item.getName(), name))
                    .map(Software::getVersion)
                    .collect(Collectors.toList());

            if (versions.isEmpty()) {
                throw new RuntimeException("Could not find version for dependency \"" + StringEscapeUtils.escapeString(name) + "\"");
            }
        }

        versions.forEach(version -> {
            String relatedPomArtifact = artifactUtils.createArtifact(groupId, artifactId, version);

            if (nonNull(scope) && Objects.equals(scope, "import")) {
                if (dependencyType == DependencyType.NORMAL) {
                    throw new UnsupportedOperationException("Import is not supported for a normal dependency");
                }

                requireNonNull(type, "type");

                if (!type.equals(POM)) {
                    throw new UnsupportedOperationException("Unexpected type \"" + StringEscapeUtils.escapeString(type) + "\"");
                }

                processRelatedPom(pomBuilder, relatedPomArtifact, softwareRepositories);
            } else {
                Software software = Software
                        .builder()
                        .type(SoftwareType.JVM)
                        .dependencyType(SoftwareDependencyType.TRANSITIVE)
                        .name(artifactUtils.createName(groupId, artifactId))
                        .version(version)
                        .build();
                if (dependencyType == DependencyType.NORMAL) {
                    pomBuilder.dependency(software);
                } else {
                    pomBuilder.dependencyManagementDependency(software);
                }
            }
        });
    }

    private Optional<Pom> processRelatedPom(Pom.PomBuilder pomBuilder, String relatedPomArtifact, Set<SoftwareRepository> softwareRepositories) {
        PomOutcome pomOutcome = fetchPom(relatedPomArtifact, softwareRepositories);

        if (pomOutcome.isJarOnly()) {
            return Optional.empty();
        }

        Pom relatedPom = pomOutcome.getPom();
        pomBuilder.transitiveArtifactCoordinates(relatedPom.getArtifactCoordinates());
        if (nonNull(relatedPom.getTransitiveArtifactCoordinates())) {
            pomBuilder.transitiveArtifactCoordinates(relatedPom.getTransitiveArtifactCoordinates());
        }
        if (nonNull(relatedPom.getDependencyManagementDependencies())) {
            pomBuilder.dependencyManagementDependencies(relatedPom.getDependencyManagementDependencies());
        }
        if (nonNull(relatedPom.getDependencies())) {
            pomBuilder.dependencies(relatedPom.getDependencies());
        }
        return Optional.of(relatedPom);
    }

    private String getNonEmptyElementWithPropertyExpansion(String name, String value, Pom pom) {
        return StringUtils.requireNonEmpty(expandProperties(name, value, pom), name);
    }

    private String expandProperties(String name, String value, Pom pom) {
        return propertyExpander.expandProperties(value, name, pom.getProperties(), true);
    }

    private String requireNoPropertyReferences(String name, String value) {
        if (nonNull(value) && value.contains("$")) {
            throw new UnsupportedOperationException(name + " with value \"" + StringEscapeUtils.escapeString(value)
                    + "\" not expected to contain property references");
        }

        return value;
    }

    private void addProperties(Map<String, String> properties, Project project) {
        Optional.of(project).map(Project::getProperties).map(Properties::getProperties)
                .ifPresent(propertyElements -> propertyElements.forEach(
                        propertyElement -> properties.put(propertyElement.getLocalName(), propertyElement.getTextContent())));
        properties.put("project.groupId", getProjectValue(project, "groupId", ProjectCoordinates::getGroupId));
        properties.put("project.version", getProjectValue(project, "version", ProjectCoordinates::getVersion));
    }

    private String getProjectValue(Project project, String name, Function<ProjectCoordinates, String> valueGetter) {
        Optional<String> projectValue = Optional.of((ProjectCoordinates) project).map(valueGetter);

        if (projectValue.isEmpty()) {
            projectValue = Optional.of((ProjectCoordinates) project.getParent()).map(valueGetter);

            if (projectValue.isEmpty()) {
                throw new IllegalArgumentException("POM does not contain project " + name);
            }
        }

        return projectValue.get();
    }

    private enum DependencyType {

        DEPENDENCY_MANAGEMENT,
        NORMAL

    }
}
