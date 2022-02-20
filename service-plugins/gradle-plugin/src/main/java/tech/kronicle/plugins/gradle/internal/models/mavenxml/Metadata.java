package tech.kronicle.plugins.gradle.internal.models.mavenxml;

import tech.kronicle.plugins.gradle.internal.models.mavenxml.metadata.Versioning;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Metadata {

    @XmlElement
    String groupId;
    @XmlElement
    String artifactId;
    @XmlElement
    String version;
    @XmlElement
    Versioning versioning;
}
