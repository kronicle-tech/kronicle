package com.moneysupermarket.componentcatalog.sdk.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.moneysupermarket.componentcatalog.sdk.models.git.GitRepo;
import com.moneysupermarket.componentcatalog.sdk.models.gradle.Gradle;
import com.moneysupermarket.componentcatalog.sdk.models.linesofcode.LinesOfCode;
import com.moneysupermarket.componentcatalog.sdk.models.openapi.OpenApiSpec;
import com.moneysupermarket.componentcatalog.sdk.models.readme.Readme;
import com.moneysupermarket.componentcatalog.sdk.models.sonarqube.SonarQubeProject;
import com.moneysupermarket.componentcatalog.sdk.models.todos.ToDo;
import com.moneysupermarket.componentcatalog.sdk.models.zipkin.Zipkin;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

import static com.moneysupermarket.componentcatalog.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class Component implements ObjectWithId, ObjectWithReference {

    @NotBlank
    @Pattern(regexp = "[a-z][a-z0-9]*(-[a-z0-9]+)*")
    String id;
    @NotBlank
    String name;
    @NotBlank
    @Pattern(regexp = "[a-z][a-z0-9]*(-[a-z0-9]+)*")
    @JsonAlias("type")
    String typeId;
    List<@NotBlank @Pattern(regexp = "[a-z][a-z0-9]*(-[a-z0-9]+)*") String> tags;
    @Valid
    @NotNull
    Repo repo;
    String description;
    List<@Valid Responsibility> responsibilities;
    String notes;
    List<@Valid Link> links;
    List<@Valid ComponentTeam> teams;
    @Pattern(regexp = "[a-z][a-z0-9]*(-[a-z0-9]+)*")
    @JsonAlias("platform")
    String platformId;
    List<@Valid ComponentDependency> dependencies;
    List<@Valid CrossFunctionalRequirement> crossFunctionalRequirements;
    List<@Valid TechDebt> techDebts;
    @Valid GitRepo gitRepo;
    @Valid Gradle gradle;
    List<@Valid SoftwareRepository> softwareRepositories;
    List<@Valid Software> software;
    List<@Valid Import> imports;
    List<@Valid KeySoftware> keySoftware;
    @Valid LinesOfCode linesOfCode;
    @Valid List<ToDo> toDos;
    @Valid Readme readme;
    @Valid Zipkin zipkin;
    List<@Valid OpenApiSpec> openApiSpecs;
    List<@Valid SonarQubeProject> sonarQubeProjects;
    List<@Valid ScannerError> scannerErrors;
    List<@Valid TestResult> testResults;

    public Component(String id, String name, String typeId, List<String> tags, Repo repo, String description, List<Responsibility> responsibilities,
            String notes, List<Link> links, List<ComponentTeam> teams, String platformId, List<ComponentDependency> dependencies,
            List<CrossFunctionalRequirement> crossFunctionalRequirements, List<TechDebt> techDebts, GitRepo gitRepo, Gradle gradle,
            List<SoftwareRepository> softwareRepositories, List<Software> software, List<Import> imports, List<KeySoftware> keySoftware,
            LinesOfCode linesOfCode, List<ToDo> toDos, Readme readme, Zipkin zipkin, List<OpenApiSpec> openApiSpecs, List<SonarQubeProject> sonarQubeProjects,
            List<ScannerError> scannerErrors, List<TestResult> testResults) {
        this.id = id;
        this.name = name;
        this.typeId = typeId;
        this.tags = createUnmodifiableList(tags);
        this.repo = repo;
        this.description = description;
        this.responsibilities = createUnmodifiableList(responsibilities);
        this.notes = notes;
        this.links = links;
        this.teams = createUnmodifiableList(teams);
        this.platformId = platformId;
        this.dependencies = createUnmodifiableList(dependencies);
        this.crossFunctionalRequirements = createUnmodifiableList(crossFunctionalRequirements);
        this.techDebts = createUnmodifiableList(techDebts);
        this.gitRepo = gitRepo;
        this.gradle = gradle;
        this.softwareRepositories = createUnmodifiableList(softwareRepositories);
        this.software = createUnmodifiableList(software);
        this.imports = createUnmodifiableList(imports);
        this.keySoftware = createUnmodifiableList(keySoftware);
        this.linesOfCode = linesOfCode;
        this.toDos = createUnmodifiableList(toDos);
        this.readme = readme;
        this.zipkin = zipkin;
        this.openApiSpecs = createUnmodifiableList(openApiSpecs);
        this.sonarQubeProjects = createUnmodifiableList(sonarQubeProjects);
        this.scannerErrors = createUnmodifiableList(scannerErrors);
        this.testResults = createUnmodifiableList(testResults);
    }

    @Override
    public String reference() {
        return id;
    }
}
