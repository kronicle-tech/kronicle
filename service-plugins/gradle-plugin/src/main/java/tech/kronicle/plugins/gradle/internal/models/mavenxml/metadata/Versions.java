package tech.kronicle.plugins.gradle.internal.models.mavenxml.metadata;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Versions {

    @XmlElement(name = "version")
    List<String> versions;
}
