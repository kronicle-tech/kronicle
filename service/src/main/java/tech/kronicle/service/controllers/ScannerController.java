package tech.kronicle.service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import tech.kronicle.sdk.models.GetScannerResponse;
import tech.kronicle.sdk.models.GetScannersResponse;
import tech.kronicle.service.partialresponse.PartialResponse;
import tech.kronicle.service.services.ComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.kronicle.service.springdoc.Texts;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/scanners")
public class ScannerController {

    private final ComponentService componentService;

    @Operation(
            tags = {"Scanners"},
            summary = "Get Scanners",
            description = "Retrieves a list of all scanners.  " + Texts.USING_FIELDS_QUERY_PARAM,
            operationId = "get-scanners"
    )
    @GetMapping
    @PartialResponse
    public GetScannersResponse getScanners() {
        return new GetScannersResponse(componentService.getScanners());
    }

    @Operation(
            tags = {"Scanners"},
            summary = "Get Scanner",
            description = "Retrieves a scanner.  " + Texts.USING_FIELDS_QUERY_PARAM,
            operationId = "get-scanner"
    )
    @GetMapping("/{scannerId}")
    @PartialResponse
    public GetScannerResponse getScanner(@PathVariable String scannerId) {
        return new GetScannerResponse(componentService.getScanner(scannerId));
    }
}
