package tech.kronicle.plugins.gradle.internal.services;

import lombok.Getter;

@Getter
public class DownloaderException extends RuntimeException {

    private final String httpMethod;
    private final String uri;
    private final int statusCode;
    private final String responseBody;

    public DownloaderException(String httpMethod, String uri, int statusCode, String responseBody) {
        super(httpMethod + " call to '" + uri + "' failed with status " + statusCode);
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}