package com.moneysupermarket.componentcatalog.service.scanners.openapi;

import com.moneysupermarket.componentcatalog.componentmetadata.models.ComponentMetadata;
import com.moneysupermarket.componentcatalog.sdk.models.ScannerError;
import com.moneysupermarket.componentcatalog.sdk.models.openapi.OpenApiSpec;
import com.moneysupermarket.componentcatalog.service.scanners.ComponentAndCodebaseScanner;
import com.moneysupermarket.componentcatalog.service.scanners.models.ComponentAndCodebase;
import com.moneysupermarket.componentcatalog.service.scanners.models.Output;
import com.moneysupermarket.componentcatalog.service.scanners.openapi.models.SpecAndErrors;
import com.moneysupermarket.componentcatalog.service.scanners.openapi.services.SpecDiscoverer;
import com.moneysupermarket.componentcatalog.service.scanners.openapi.services.SpecParser;
import com.moneysupermarket.componentcatalog.service.spring.stereotypes.Scanner;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Scanner
@RequiredArgsConstructor
@Slf4j
public class OpenApiScanner extends ComponentAndCodebaseScanner {

    private final SpecDiscoverer specDiscoverer;
    private final SpecParser specParser;

    @Override
    public String id() {
        return "openapi";
    }

    @Override
    public String description() {
        return "This does two things: a) scan's a component's codebase for any YAML or JSON files that contain OpenAPI specs and b) uses any OpenAPI spec URLs "
                + "specified in a component's metadata";
    }

    @Override
    public void refresh(ComponentMetadata componentMetadata) {
        specParser.clearCache();
    }

    @Override
    public Output<Void> scan(ComponentAndCodebase input) {
        ScanOutput scanOutput = processComponentAndCodebase(input);
        return Output.of(component -> component.withOpenApiSpecs(scanOutput.getSpecs()), scanOutput.getErrors());
    }

    private ScanOutput processComponentAndCodebase(ComponentAndCodebase input) {
        List<OpenApiSpec> specs = getManualSpecs(input);
        specDiscoverer.discoverSpecsInCodebase(this, input, specs);

        List<SpecAndErrors> specAndErrors = specParser.parseSpecs(this, input, specs);
        List<OpenApiSpec> newSpecs = getSpecs(specAndErrors);
        if (log.isInfoEnabled()) {
            log.info("Found {} OpenAPI specs for component {}", countSuccessfullyParsedSpecs(newSpecs), input.getComponent().getId());
        }
        return new ScanOutput(newSpecs, getErrors(specAndErrors));
    }

    private List<OpenApiSpec> getManualSpecs(ComponentAndCodebase input) {
        return input.getComponent().getOpenApiSpecs().stream()
                .filter(this::isManualSpec)
                .collect(Collectors.toList());
    }

    private List<OpenApiSpec> getSpecs(List<SpecAndErrors> specAndErrors) {
        return specAndErrors.stream()
                .filter(this::isManualSpecOrWasParsedSuccessfully)
                .map(SpecAndErrors::getSpec)
                .collect(Collectors.toList());
    }

    private List<ScannerError> getErrors(List<SpecAndErrors> specAndErrors) {
        return specAndErrors.stream()
                .map(SpecAndErrors::getErrors)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private boolean isManualSpecOrWasParsedSuccessfully(SpecAndErrors specAndErrors) {
        return isManualSpec(specAndErrors) || wasParsedSuccessfully(specAndErrors);
    }

    private boolean wasParsedSuccessfully(SpecAndErrors specAndErrors) {
        return wasParsedSuccessfully(specAndErrors.getSpec());
    }

    private boolean wasParsedSuccessfully(OpenApiSpec spec) {
        return nonNull(spec.getSpec());
    }

    private boolean isManualSpec(SpecAndErrors specAndErrors) {
        return isManualSpec(specAndErrors.getSpec());
    }

    private boolean isManualSpec(OpenApiSpec spec) {
        return !Objects.equals(spec.getScannerId(), id());
    }

    private int countSuccessfullyParsedSpecs(List<OpenApiSpec> newSpecs) {
        return (int) newSpecs.stream().filter(this::wasParsedSuccessfully).count();
    }

    @Value
    private static class ScanOutput {

        List<OpenApiSpec> specs;
        List<ScannerError> errors;
    }
}
