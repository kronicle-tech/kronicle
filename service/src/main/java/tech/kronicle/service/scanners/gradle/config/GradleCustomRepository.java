package tech.kronicle.service.scanners.gradle.config;

import lombok.Value;

@Value
public class GradleCustomRepository {

    String name;
    String url;
}
