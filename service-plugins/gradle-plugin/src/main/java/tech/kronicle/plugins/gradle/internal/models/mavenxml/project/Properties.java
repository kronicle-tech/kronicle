package tech.kronicle.plugins.gradle.internal.models.mavenxml.project;

import lombok.Data;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Properties {

    @XmlAnyElement
    private List<Element> properties;
}
