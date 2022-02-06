package tech.kronicle.service.services;

import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tech.kronicle.service.constants.Resilience4JInstanceNames;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;

import java.util.function.Function;

@Service
public class HttpRequestMaker {

    @Retry(name = Resilience4JInstanceNames.HTTP_REQUEST_MAKER)
    public ResponseEntity<String> makeHttpRequest(Function<String, ResponseEntity<String>> httpRequest, String url) {
        return httpRequest.apply(url);
    }
}
