package tech.kronicle.plugins.sonarqube;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.scanners.ComponentAndCodebaseScanner;
import tech.kronicle.pluginapi.scanners.models.ComponentAndCodebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.sonarqube.exceptions.SonarQubeScannerException;
import tech.kronicle.plugins.sonarqube.services.SonarQubeService;
import tech.kronicle.sdk.models.*;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;

import javax.inject.Inject;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SonarQubeScanner extends ComponentAndCodebaseScanner {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    private final SonarQubeService service;

    @Override
    public String id() {
        return "sonarqube";
    }

    @Override
    public String description() {
        return "Scans a component's codebase looking for any references to SonarQube project keys.  For any references it finds, it will call the SonarQube "
                + "server's API to retrieve all the latest metrics for those SonarQube projects";
    }

    @Override
    public void refresh(ComponentMetadata componentMetadata) {
        service.refresh();
    }

    @Override
    public Output<Void, Component> scan(ComponentAndCodebase input) {
        try {
            List<SonarQubeProject> projects = service.findProjects(input.getCodebase().getDir());

            if (projects.isEmpty()) {
                return Output.empty(CACHE_TTL);
            }

            return createOutput(projects);
        } catch (SonarQubeScannerException e) {
            return createOutput(e);
        }
    }

    @Override
    public Summary transformSummary(Summary summary) {
        return summary.withSonarQubeMetrics(service.getMetrics())
                .withMissingComponents(addMissingComponents(summary.getMissingComponents()));
    }

    private List<SummaryMissingComponent> addMissingComponents(List<SummaryMissingComponent> missingComponents) {
        List<SummaryMissingComponent> newList = new ArrayList<>(missingComponents);
        newList.addAll(service.getMissingComponents(id()));
        return newList;
    }

    private Output<Void, Component> createOutput(List<SonarQubeProject> sonarQubeProjects) {
        return Output.ofTransformer(component -> component.addState(createState(sonarQubeProjects)), CACHE_TTL);
    }

    private SonarQubeProjectsState createState(List<SonarQubeProject> sonarQubeProjects) {
        return new SonarQubeProjectsState(
                SonarQubePlugin.ID,
                sonarQubeProjects
        );
    }

    private Output<Void, Component> createOutput(SonarQubeScannerException e) {
        return Output.ofError(ScannerError.builder().scannerId(id()).message(e.getMessage()).build(), CACHE_TTL);
    }
}
