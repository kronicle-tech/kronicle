package tech.kronicle.service.controllers;

import tech.kronicle.sdk.models.GetTestResponse;
import tech.kronicle.sdk.models.GetTestsResponse;
import tech.kronicle.service.partialresponse.PartialResponse;
import tech.kronicle.service.services.ComponentService;
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
