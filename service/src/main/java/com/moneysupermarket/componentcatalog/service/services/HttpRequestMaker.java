package com.moneysupermarket.componentcatalog.service.services;

import com.moneysupermarket.componentcatalog.service.constants.Resilience4JInstanceNames;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;

import java.util.function.Function;

@Service
public class HttpRequestMaker {

    @Retry(name = Resilience4JInstanceNames.HTTP_REQUEST_MAKER)
    public ClientResponse makeHttpRequest(Function<String, ClientResponse> httpRequest, String url) {
        return httpRequest.apply(url);
    }
}
