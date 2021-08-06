package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.models.mavenxml.metadata;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

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
