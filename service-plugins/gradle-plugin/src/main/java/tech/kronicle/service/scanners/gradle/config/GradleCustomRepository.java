package tech.kronicle.service.scanners.gradle.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import tech.kronicle.service.models.HttpHeader;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
public class GradleCustomRepository {

    @NotEmpty
    String name;
    @NotEmpty
    String url;
    List<HttpHeader> httpHeaders;
}
