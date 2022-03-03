package tech.kronicle.plugins.gitlab.client;

import lombok.Getter;

@Getter
public class GitLabClientException extends RuntimeException {

    private final String uri;
    private final int statusCode;
    private final String responseBody;

    public GitLabClientException(String uri, int statusCode, String responseBody) {
        super("Call to '" + uri + "' failed with status " + statusCode);
        this.uri = uri;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}