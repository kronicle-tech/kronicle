package tech.kronicle.service.controllers;

import tech.kronicle.sdk.models.GetSummaryResponse;
import tech.kronicle.service.partialresponse.PartialResponse;
import tech.kronicle.service.services.ComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/summary")
public class SummaryController {

    private final ComponentService componentService;

    @GetMapping
    @PartialResponse
    public GetSummaryResponse getSummary() {
        return new GetSummaryResponse(componentService.getSummary());
    }
}
