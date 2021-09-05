package tech.kronicle.service.services;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@Import(HttpRequestMaker.class)
public class HttpRequestMakerTestConfiguration {

}
