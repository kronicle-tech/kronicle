package com.moneysupermarket.componentcatalog.service.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Validated
@ConfigurationProperties("url-exists-cache")
@ConstructorBinding
@Value
@NonFinal
public class UrlExistsCacheConfig {

    @NotEmpty
    String dir;
}
