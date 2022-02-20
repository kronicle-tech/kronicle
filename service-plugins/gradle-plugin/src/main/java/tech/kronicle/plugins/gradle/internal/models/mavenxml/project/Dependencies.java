package tech.kronicle.plugins.gradle.internal.models.mavenxml.project;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Dependencies {

    @XmlElement(name = "dependency")
    private List<Dependency> dependencies;
}
