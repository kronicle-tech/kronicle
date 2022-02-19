package tech.kronicle.plugins.gradle.internal.models.mavenxml.project;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import tech.kronicle.plugins.gradle.internal.models.mavenxml.ProjectCoordinates;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Parent implements ProjectCoordinates {

    @XmlElement
    private String groupId;
    @XmlElement
    private String artifactId;
    @XmlElement
    private String version;
    @XmlElement
    private String packaging;
}
