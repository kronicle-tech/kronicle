package tech.kronicle.service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.kronicle.service.services.ComponentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/populated")
public class PopulatedController {

    private final ComponentService componentService;

    @Operation(
            tags = {"Home"},
            summary = "Get Populated",
            description = "Returns a 200 status code if there are any components loaded, otherwise returns a 404 status code.  ",
            operationId = "get-populated"
    )
    @GetMapping
    public ResponseEntity<String> getPopulated() {
        return componentService.hasComponents()
                ? ResponseEntity.ok("Populated")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not populated");
    }
}
