package tech.kronicle.plugins.gradle.internal.models.mavenxml.project;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import tech.kronicle.plugins.gradle.internal.models.mavenxml.DependenciesContainer;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class DependencyManagement implements DependenciesContainer {

    @XmlElement
    private Dependencies dependencies;
}
