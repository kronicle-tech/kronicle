package tech.kronicle.service.scanners.gradle.config;

import lombok.Value;
import tech.kronicle.service.models.HttpHeader;

import java.util.List;

@Value
public class GradleCustomRepository {

    String name;
    String url;
    List<HttpHeader> httpHeaders;
}
