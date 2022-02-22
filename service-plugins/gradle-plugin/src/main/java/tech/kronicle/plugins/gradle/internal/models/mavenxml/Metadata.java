package tech.kronicle.plugins.gradle.internal.models.mavenxml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import tech.kronicle.plugins.gradle.internal.models.mavenxml.metadata.Versioning;
import lombok.Data;

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
