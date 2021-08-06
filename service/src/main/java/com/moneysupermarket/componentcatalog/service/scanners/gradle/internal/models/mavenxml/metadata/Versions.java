package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.models.mavenxml.metadata;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Versions {

    @XmlElement(name = "version")
    List<String> versions;
}
