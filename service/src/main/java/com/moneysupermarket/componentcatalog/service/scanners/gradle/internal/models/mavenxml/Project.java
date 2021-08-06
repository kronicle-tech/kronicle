package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.models.mavenxml;

import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.models.mavenxml.project.Dependencies;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.models.mavenxml.project.DependencyManagement;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.models.mavenxml.project.Parent;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.models.mavenxml.project.Properties;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Project implements ProjectCoordinates, DependenciesContainer {

    @XmlElement
    private String modelVersion;
    @XmlElement
    private String groupId;
    @XmlElement
    private String artifactId;
    @XmlElement
    private String version;
    @XmlElement
    private String packaging;
    @XmlElement
    private String name;
    @XmlElement
    private Parent parent;
    @XmlElement
    private Dependencies dependencies;
    @XmlElement
    private DependencyManagement dependencyManagement;
    @XmlElement
    private Properties properties;
}
