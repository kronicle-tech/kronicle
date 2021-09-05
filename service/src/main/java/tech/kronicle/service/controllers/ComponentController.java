package tech.kronicle.service.controllers;

import tech.kronicle.sdk.models.GetComponentResponse;
import tech.kronicle.sdk.models.GetComponentsResponse;
import tech.kronicle.sdk.models.TestOutcome;
import tech.kronicle.service.partialresponse.PartialResponse;
import tech.kronicle.service.services.ComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.kronicle.service.utils.EnumUtils;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/components")
public class ComponentController {

    private final ComponentService componentService;

    @GetMapping
    @PartialResponse
    public GetComponentsResponse getComponents(@RequestParam(required = false) Optional<Integer> offset,
            @RequestParam(required = false) Optional<Integer> limit, @RequestParam(required = false) List<String> testOutcome) {
        return new GetComponentsResponse(componentService.getComponents(offset, limit, EnumUtils.getEnumListFromJsonValues(TestOutcome.class, testOutcome)));
    }

    @GetMapping("/{componentId}")
    @PartialResponse
    public GetComponentResponse getComponent(@PathVariable String componentId) {
        return new GetComponentResponse(componentService.getComponent(componentId));
    }
}
