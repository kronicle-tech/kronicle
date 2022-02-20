package tech.kronicle.plugins.gradle.config;

import lombok.Value;
import tech.kronicle.service.models.HttpHeader;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Value
public class GradleCustomRepository {

    @NotEmpty
    String name;
    @NotEmpty
    String url;
    List<HttpHeader> httpHeaders;
}
