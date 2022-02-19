package tech.kronicle.service.scanners.gradle.internal.models.mavenxml.project;

import tech.kronicle.service.scanners.gradle.internal.models.mavenxml.DependenciesContainer;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class DependencyManagement implements DependenciesContainer {

    @XmlElement
    private Dependencies dependencies;
}
