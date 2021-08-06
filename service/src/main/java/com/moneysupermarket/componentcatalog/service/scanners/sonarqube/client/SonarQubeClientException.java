package com.moneysupermarket.componentcatalog.service.scanners.sonarqube.client;

import lombok.Getter;

@Getter
public class SonarQubeClientException extends RuntimeException {

    private final String endpointName;
    private final int statusCode;
    private final String responseBody;

    public SonarQubeClientException(String endpointName, int statusCode, String responseBody) {
        super("Call to '" + endpointName + "' failed with status " + statusCode);
        this.endpointName = endpointName;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}