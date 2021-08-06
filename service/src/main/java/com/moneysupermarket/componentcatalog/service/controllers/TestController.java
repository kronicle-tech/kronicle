package com.moneysupermarket.componentcatalog.service.controllers;

import com.moneysupermarket.componentcatalog.sdk.models.GetTestResponse;
import com.moneysupermarket.componentcatalog.sdk.models.GetTestsResponse;
import com.moneysupermarket.componentcatalog.service.partialresponse.PartialResponse;
import com.moneysupermarket.componentcatalog.service.services.ComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/tests")
public class TestController {

    private final ComponentService componentService;

    @GetMapping
    @PartialResponse
    public GetTestsResponse getTests() {
        return new GetTestsResponse(componentService.getTests());
    }

    @GetMapping("/{testId}")
    @PartialResponse
    public GetTestResponse getTest(@PathVariable String testId) {
        return new GetTestResponse(componentService.getTest(testId));
    }
}
