package tech.kronicle.sdk.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.UniqueElements;
import tech.kronicle.sdk.models.git.GitRepo;
import tech.kronicle.sdk.models.gradle.Gradle;
import tech.kronicle.sdk.models.linesofcode.LinesOfCode;
import tech.kronicle.sdk.models.nodejs.NodeJs;
import tech.kronicle.sdk.models.openapi.OpenApiSpec;
import tech.kronicle.sdk.models.readme.Readme;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;
import tech.kronicle.sdk.models.todos.ToDo;
import tech.kronicle.sdk.models.zipkin.Zipkin;
import tech.kronicle.sdk.utils.ListUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class Component implements ObjectWithId, ObjectWithReference {

    @NotBlank
    @Pattern(regexp = "[a-z][a-z0-9]*(-[a-z0-9]+)*")
    String id;
    @UniqueElements
    List<Alias> aliases;
    @NotBlank
    String name;
    @NotNull
    Boolean discovered;
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
    @Valid NodeJs nodeJs;
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

    public Component(
            String id,
            List<Alias> aliases,
            String name,
            Boolean discovered,
            String typeId,
            List<String> tags,
            Repo repo,
            String description,
            List<Responsibility> responsibilities,
            String notes,
            List<Link> links,
            List<ComponentTeam> teams,
            String platformId,
            List<ComponentDependency> dependencies,
            List<CrossFunctionalRequirement> crossFunctionalRequirements,
            List<TechDebt> techDebts,
            GitRepo gitRepo,
            Gradle gradle,
            NodeJs nodeJs,
            List<SoftwareRepository> softwareRepositories,
            List<Software> software,
            List<Import> imports,
            List<KeySoftware> keySoftware,
            LinesOfCode linesOfCode,
            List<ToDo> toDos,
            Readme readme,
            Zipkin zipkin,
            List<OpenApiSpec> openApiSpecs,
            List<SonarQubeProject> sonarQubeProjects,
            List<ScannerError> scannerErrors,
            List<TestResult> testResults
    ) {
        this.id = id;
        this.aliases = ListUtils.createUnmodifiableList(aliases);
        this.name = name;
        this.discovered = discovered;
        this.typeId = typeId;
        this.tags = ListUtils.createUnmodifiableList(tags);
        this.repo = repo;
        this.description = description;
        this.responsibilities = ListUtils.createUnmodifiableList(responsibilities);
        this.notes = notes;
        this.links = links;
        this.teams = ListUtils.createUnmodifiableList(teams);
        this.platformId = platformId;
        this.dependencies = ListUtils.createUnmodifiableList(dependencies);
        this.crossFunctionalRequirements = ListUtils.createUnmodifiableList(crossFunctionalRequirements);
        this.techDebts = ListUtils.createUnmodifiableList(techDebts);
        this.gitRepo = gitRepo;
        this.gradle = gradle;
        this.nodeJs = nodeJs;
        this.softwareRepositories = ListUtils.createUnmodifiableList(softwareRepositories);
        this.software = ListUtils.createUnmodifiableList(software);
        this.imports = ListUtils.createUnmodifiableList(imports);
        this.keySoftware = ListUtils.createUnmodifiableList(keySoftware);
        this.linesOfCode = linesOfCode;
        this.toDos = ListUtils.createUnmodifiableList(toDos);
        this.readme = readme;
        this.zipkin = zipkin;
        this.openApiSpecs = ListUtils.createUnmodifiableList(openApiSpecs);
        this.sonarQubeProjects = ListUtils.createUnmodifiableList(sonarQubeProjects);
        this.scannerErrors = ListUtils.createUnmodifiableList(scannerErrors);
        this.testResults = ListUtils.createUnmodifiableList(testResults);
    }

    @Override
    public String reference() {
        return id;
    }
}
