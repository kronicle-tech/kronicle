package com.moneysupermarket.componentcatalog.service.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.Duration;

@Validated
@ConfigurationProperties("downloader")
@ConstructorBinding
@Value
@NonFinal
public class DownloaderConfig {

    @NotNull
    Duration timeout;
}
