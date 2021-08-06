package com.moneysupermarket.componentcatalog.service.scanners.sonarqube;

import com.moneysupermarket.componentcatalog.componentmetadata.models.ComponentMetadata;
import com.moneysupermarket.componentcatalog.sdk.models.ScannerError;
import com.moneysupermarket.componentcatalog.sdk.models.Summary;
import com.moneysupermarket.componentcatalog.sdk.models.SummaryMissingComponent;
import com.moneysupermarket.componentcatalog.sdk.models.sonarqube.SonarQubeProject;
import com.moneysupermarket.componentcatalog.service.scanners.ComponentAndCodebaseScanner;
import com.moneysupermarket.componentcatalog.service.scanners.models.ComponentAndCodebase;
import com.moneysupermarket.componentcatalog.service.scanners.models.Output;
import com.moneysupermarket.componentcatalog.service.scanners.sonarqube.exceptions.SonarQubeScannerException;
import com.moneysupermarket.componentcatalog.service.scanners.sonarqube.services.SonarQubeService;
import com.moneysupermarket.componentcatalog.service.spring.stereotypes.Scanner;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Scanner
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
    public void refresh(ComponentMetadata componentMetadata) {
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
