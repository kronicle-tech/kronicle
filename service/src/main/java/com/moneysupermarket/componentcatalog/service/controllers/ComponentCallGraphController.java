package com.moneysupermarket.componentcatalog.service.controllers;

import com.moneysupermarket.componentcatalog.sdk.models.GetComponentCallGraphsResponse;
import com.moneysupermarket.componentcatalog.service.partialresponse.PartialResponse;
import com.moneysupermarket.componentcatalog.service.services.ComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/components/{componentId}")
public class ComponentCallGraphController {

    private final ComponentService componentService;

    @GetMapping("/call-graphs")
    @PartialResponse
    public GetComponentCallGraphsResponse getComponentCallGraphs(@PathVariable String componentId) {
        return new GetComponentCallGraphsResponse(componentService.getComponentCallGraphs(componentId));
    }
}
