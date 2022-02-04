package tech.kronicle.service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.kronicle.service.partialresponse.PartialResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class HealthController {

    @Operation(
            tags = {"Home"},
            summary = "Get Health",
            description = "Returns the word OK.  ",
            operationId = "get-health"
    )
    @GetMapping
    @PartialResponse
    public String getHealth() {
        return "OK";
    }
}
