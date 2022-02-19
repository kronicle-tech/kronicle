package tech.kronicle.plugins.gradle.internal.models.mavenxml.metadata;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Versioning {

    @XmlElement
    String latest;
    @XmlElement
    String release;
    @XmlElement
    Versions versions;
}
