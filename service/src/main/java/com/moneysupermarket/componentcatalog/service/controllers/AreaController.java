package com.moneysupermarket.componentcatalog.service.controllers;

import com.moneysupermarket.componentcatalog.sdk.models.GetAreaResponse;
import com.moneysupermarket.componentcatalog.sdk.models.GetAreasResponse;
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

import static com.moneysupermarket.componentcatalog.service.utils.EnumUtils.getEnumListFromJsonValues;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/areas")
public class AreaController {

    private final ComponentService componentService;

    @GetMapping
    @PartialResponse
    public GetAreasResponse getAreas(@RequestParam(required = false) List<String> testOutcome) {
        return new GetAreasResponse(componentService.getAreas(getEnumListFromJsonValues(TestOutcome.class, testOutcome)));
    }

    @GetMapping("/{areaId}")
    @PartialResponse
    public GetAreaResponse getArea(@PathVariable String areaId, @RequestParam(required = false) List<String> testOutcome) {
        return new GetAreaResponse(componentService.getArea(areaId, getEnumListFromJsonValues(TestOutcome.class, testOutcome)));
    }
}
