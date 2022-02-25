package tech.kronicle.plugins.gradle.internal.models.mavenxml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import tech.kronicle.plugins.gradle.internal.models.mavenxml.project.Dependencies;
import tech.kronicle.plugins.gradle.internal.models.mavenxml.project.DependencyManagement;
import tech.kronicle.plugins.gradle.internal.models.mavenxml.project.Parent;
import tech.kronicle.plugins.gradle.internal.models.mavenxml.project.Properties;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Project implements ProjectCoordinates, DependenciesContainer {

    @XmlElement
    private String modelVersion;
    @XmlElement
    private String groupId;
    @XmlElement
    private String artifactId;
    @XmlElement
    private String version;
    @XmlElement
    private String packaging;
    @XmlElement
    private String name;
    @XmlElement
    private Parent parent;
    @XmlElement
    private Dependencies dependencies;
    @XmlElement
    private DependencyManagement dependencyManagement;
    @XmlElement
    private Properties properties;
}
