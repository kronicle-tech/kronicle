package tech.kronicle.service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class HomeController {

    @Operation(
            tags = {"Home"},
            summary = "Get Home",
            description = "Returns the word OK.  ",
            operationId = "get-home"
    )
    @GetMapping
    public String getHome() {
        return "OK";
    }
}
