package com.moneysupermarket.componentcatalog.service.partialresponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidFieldsQueryParamException extends ResponseStatusException {

    public InvalidFieldsQueryParamException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }

    public InvalidFieldsQueryParamException(String reason, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, reason, cause);
    }
}
