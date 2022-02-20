package tech.kronicle.plugins.gradle.internal.models.mavenxml.project;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Dependency {

    @XmlElement
    private String groupId;
    @XmlElement
    private String artifactId;
    @XmlElement
    private String version;
    @XmlElement
    private String type;
    @XmlElement
    private String scope;
}
