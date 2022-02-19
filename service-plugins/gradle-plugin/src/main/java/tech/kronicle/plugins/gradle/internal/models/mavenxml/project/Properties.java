package tech.kronicle.plugins.gradle.internal.models.mavenxml.project;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import lombok.Data;
import org.w3c.dom.Element;

import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Properties {

    @XmlAnyElement
    private List<Element> properties;
}
