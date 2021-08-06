package com.moneysupermarket.componentcatalog.service.controllers;

import com.moneysupermarket.componentcatalog.sdk.models.GetComponentResponse;
import com.moneysupermarket.componentcatalog.sdk.models.GetComponentsResponse;
import com.moneysupermarket.componentcatalog.sdk.models.TestOutcome;
import com.moneysupermarket.componentcatalog.service.partialresponse.PartialResponse;
import com.moneysupermarket.componentcatalog.service.services.ComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static com.moneysupermarket.componentcatalog.service.utils.EnumUtils.getEnumListFromJsonValues;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/components")
public class ComponentController {

    private final ComponentService componentService;

    @GetMapping
    @PartialResponse
    public GetComponentsResponse getComponents(@RequestParam(required = false) Optional<Integer> offset,
            @RequestParam(required = false) Optional<Integer> limit, @RequestParam(required = false) List<String> testOutcome) {
        return new GetComponentsResponse(componentService.getComponents(offset, limit, getEnumListFromJsonValues(TestOutcome.class, testOutcome)));
    }

    @GetMapping("/{componentId}")
    @PartialResponse
    public GetComponentResponse getComponent(@PathVariable String componentId) {
        return new GetComponentResponse(componentService.getComponent(componentId));
    }
}
