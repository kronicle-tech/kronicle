package com.moneysupermarket.componentcatalog.service.controllers;

import com.moneysupermarket.componentcatalog.sdk.models.GetScannerResponse;
import com.moneysupermarket.componentcatalog.sdk.models.GetScannersResponse;
import com.moneysupermarket.componentcatalog.service.partialresponse.PartialResponse;
import com.moneysupermarket.componentcatalog.service.services.ComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/scanners")
public class ScannerController {

    private final ComponentService componentService;

    @GetMapping
    @PartialResponse
    public GetScannersResponse getScanners() {
        return new GetScannersResponse(componentService.getScanners());
    }

    @GetMapping("/{scannerId}")
    @PartialResponse
    public GetScannerResponse getScanner(@PathVariable String scannerId) {
        return new GetScannerResponse(componentService.getScanner(scannerId));
    }
}
