package tech.kronicle.service.scanners.openapi;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.componentmetadata.models.ComponentMetadata;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.sdk.models.ScannerError;
import tech.kronicle.sdk.models.openapi.OpenApiSpec;
import tech.kronicle.service.scanners.ComponentAndCodebaseScanner;
import tech.kronicle.service.scanners.models.ComponentAndCodebase;
import tech.kronicle.service.scanners.models.Output;
import tech.kronicle.service.scanners.openapi.models.SpecAndErrors;
import tech.kronicle.service.scanners.openapi.services.SpecDiscoverer;
import tech.kronicle.service.scanners.openapi.services.SpecParser;
import tech.kronicle.service.scanners.openapi.utils.OpenApiSpecUtils;
import tech.kronicle.service.spring.stereotypes.Scanner;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static tech.kronicle.service.scanners.openapi.utils.OpenApiSpecUtils.isManualSpec;

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
    public void refresh(ComponentMetadata componentMetadata, List<Dependency> dependencies) {
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
                .filter(OpenApiSpecUtils::isManualSpec)
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
                .filter(OpenApiSpecUtils::isManualSpec)
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

    private int countSuccessfullyParsedSpecs(List<OpenApiSpec> newSpecs) {
        return (int) newSpecs.stream().filter(this::wasParsedSuccessfully).count();
    }

    @Value
    private static class ScanOutput {

        List<OpenApiSpec> specs;
        List<ScannerError> errors;
    }
}
