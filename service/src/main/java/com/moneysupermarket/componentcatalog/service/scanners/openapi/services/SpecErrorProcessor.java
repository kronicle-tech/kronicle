package com.moneysupermarket.componentcatalog.service.scanners.openapi.services;

import com.moneysupermarket.componentcatalog.sdk.models.ScannerError;
import com.moneysupermarket.componentcatalog.service.mappers.ThrowableToScannerErrorMapper;
import com.moneysupermarket.componentcatalog.service.scanners.Scanner;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.moneysupermarket.componentcatalog.common.utils.StringEscapeUtils.escapeString;

@Service
@RequiredArgsConstructor
public class SpecErrorProcessor {

    private static final List<String> OK_MESSAGES = List.of("attribute openapi is missing", "attribute openapi is not of type `object`");

    private final ThrowableToScannerErrorMapper throwableToScannerErrorMapper;

    public ScannerError getError(Scanner scanner, String location, Exception e) {
        return throwableToScannerErrorMapper.map(scanner.id(), createMessagePrefix(location), e);
    }

    public List<ScannerError> getErrors(Scanner scanner, String location, SwaggerParseResult swaggerParseResult) {
        return swaggerParseResult.getMessages().stream()
                .filter(this::isErrorMessage)
                .map(createError(scanner, location))
                .collect(Collectors.toList());
    }

    private boolean isErrorMessage(String message) {
        return !OK_MESSAGES.contains(message);
    }

    private Function<String, ScannerError> createError(Scanner scanner, String location) {
        return (String message) -> new ScannerError(scanner.id(), createMessagePrefix(location) + message, null);
    }

    private String createMessagePrefix(String location) {
        return String.format("Issue while parsing OpenAPI spec \"%s\": ", escapeString(location));
    }
}
