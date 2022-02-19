package tech.kronicle.plugins.sonarqube;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import tech.kronicle.componentmetadata.models.ComponentMetadata;
import tech.kronicle.pluginapi.scanners.ComponentAndCodebaseScanner;
import tech.kronicle.pluginapi.scanners.models.ComponentAndCodebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.sonarqube.exceptions.SonarQubeScannerException;
import tech.kronicle.plugins.sonarqube.services.SonarQubeService;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.sdk.models.ScannerError;
import tech.kronicle.sdk.models.Summary;
import tech.kronicle.sdk.models.SummaryMissingComponent;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;

import java.util.ArrayList;
import java.util.List;

@Extension
@Component
@RequiredArgsConstructor
public class SonarQubeScanner extends ComponentAndCodebaseScanner {

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
    public void refresh(ComponentMetadata componentMetadata, List<Dependency> dependencies) {
        service.refresh();
    }

    @Override
    public Output<Void> scan(ComponentAndCodebase input) {
        try {
            return createOutput(service.findProjects(input.getCodebase().getDir()));
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

    private Output<Void> createOutput(List<SonarQubeProject> sonarQubeProjects) {
        return Output.of(component -> component.withSonarQubeProjects(sonarQubeProjects));
    }

    private Output<Void> createOutput(SonarQubeScannerException e) {
        return Output.of(ScannerError.builder().scannerId(id()).message(e.getMessage()).build());
    }
}
