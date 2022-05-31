package tech.kronicle.sdk.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.UniqueElements;
import tech.kronicle.sdk.constants.PatternStrings;
import tech.kronicle.sdk.jackson.TagOrStringDeserializer;
import tech.kronicle.sdk.models.git.GitRepo;
import tech.kronicle.sdk.models.gradle.Gradle;
import tech.kronicle.sdk.models.graphql.GraphQlSchema;
import tech.kronicle.sdk.models.linesofcode.LinesOfCodeState;
import tech.kronicle.sdk.models.nodejs.NodeJs;
import tech.kronicle.sdk.models.openapi.OpenApiSpec;
import tech.kronicle.sdk.models.readme.Readme;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;
import tech.kronicle.sdk.models.todos.ToDo;
import tech.kronicle.sdk.models.zipkin.Zipkin;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;
import static tech.kronicle.sdk.utils.ListUtils.unmodifiableUnionOfLists;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class Component implements ObjectWithId, ObjectWithReference {

    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String id;
    @UniqueElements
    List<Alias> aliases;
    @NotBlank
    String name;
    Boolean discovered;
    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    @JsonAlias("type")
    String typeId;
    @JsonDeserialize(contentUsing = TagOrStringDeserializer.class)
    List<@Valid Tag> tags;
    @Valid
    RepoReference repo;
    String description;
    List<@Valid Responsibility> responsibilities;
    String notes;
    List<@Valid Link> links;
    List<@Valid ComponentTeam> teams;
    @Pattern(regexp = PatternStrings.ID)
    @JsonAlias("platform")
    String platformId;
    List<@Valid ComponentDependency> dependencies;
    List<@Valid CrossFunctionalRequirement> crossFunctionalRequirements;
    List<@Valid TechDebt> techDebts;

    List<@Valid ComponentState> states;
    
    @Valid GitRepo gitRepo;
    @Valid Gradle gradle;
    @Valid NodeJs nodeJs;
    List<@Valid SoftwareRepository> softwareRepositories;
    List<@Valid Software> software;
    List<@Valid Import> imports;
    List<@Valid KeySoftware> keySoftware;
    @Valid List<ToDo> toDos;
    @Valid Readme readme;
    @Valid Zipkin zipkin;
    List<@Valid OpenApiSpec> openApiSpecs;
    List<@Valid GraphQlSchema> graphQlSchemas;
    List<@Valid SonarQubeProject> sonarQubeProjects;
    List<@Valid ScannerError> scannerErrors;
    List<@Valid TestResult> testResults;

    public Component(
            String id,
            List<Alias> aliases,
            String name,
            Boolean discovered,
            String typeId,
            List<Tag> tags,
            RepoReference repo,
            String description,
            List<Responsibility> responsibilities,
            String notes,
            List<Link> links,
            List<ComponentTeam> teams,
            String platformId,
            List<ComponentDependency> dependencies,
            List<CrossFunctionalRequirement> crossFunctionalRequirements,
            List<TechDebt> techDebts,
            List<ComponentState> states,
            GitRepo gitRepo,
            Gradle gradle,
            NodeJs nodeJs,
            List<SoftwareRepository> softwareRepositories,
            List<Software> software,
            List<Import> imports,
            List<KeySoftware> keySoftware,
            List<ToDo> toDos,
            Readme readme,
            Zipkin zipkin,
            List<OpenApiSpec> openApiSpecs,
            List<@Valid GraphQlSchema> graphQlSchemas,
            List<SonarQubeProject> sonarQubeProjects,
            List<ScannerError> scannerErrors,
            List<TestResult> testResults
    ) {
        this.id = id;
        this.aliases = createUnmodifiableList(aliases);
        this.name = name;
        this.discovered = discovered;
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
        this.states = createUnmodifiableList(states);
        this.gitRepo = gitRepo;
        this.gradle = gradle;
        this.nodeJs = nodeJs;
        this.softwareRepositories = createUnmodifiableList(softwareRepositories);
        this.software = createUnmodifiableList(software);
        this.imports = createUnmodifiableList(imports);
        this.keySoftware = createUnmodifiableList(keySoftware);
        this.toDos = createUnmodifiableList(toDos);
        this.readme = readme;
        this.zipkin = zipkin;
        this.openApiSpecs = createUnmodifiableList(openApiSpecs);
        this.graphQlSchemas = createUnmodifiableList(graphQlSchemas);
        this.sonarQubeProjects = createUnmodifiableList(sonarQubeProjects);
        this.scannerErrors = createUnmodifiableList(scannerErrors);
        this.testResults = createUnmodifiableList(testResults);
    }

    @Override
    public String reference() {
        return id;
    }

    public Component addState(ComponentState state) {
        return withStates(
                unmodifiableUnionOfLists(List.of(this.states, List.of(state)))
        );
    }

    public Component addStates(List<ComponentState> states) {
        return withStates(
                unmodifiableUnionOfLists(List.of(this.states, states))
        );
    }

    public Component addImports(List<Import> imports) {
        return withImports(
                unmodifiableUnionOfLists(List.of(this.imports, imports))
        );
    }

    public Component addSoftwareRepositories(List<SoftwareRepository> softwareRepositories) {
        return withSoftwareRepositories(
                unmodifiableUnionOfLists(List.of(this.softwareRepositories, softwareRepositories))
        );
    }

    public Component addSoftware(List<Software> software) {
        return withSoftware(
                unmodifiableUnionOfLists(List.of(this.software, software))
        );
    }
}
