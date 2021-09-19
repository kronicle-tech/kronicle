package tech.kronicle.service.repofinders.bitbucketserver.client;

import lombok.Getter;

@Getter
public class BitbucketServerClientException extends RuntimeException {

    private final String uri;
    private final int statusCode;
    private final String responseBody;

    public BitbucketServerClientException(String uri, int statusCode, String responseBody) {
        super("Call to '" + uri + "' failed with status " + statusCode);
        this.uri = uri;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}