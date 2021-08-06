package com.moneysupermarket.componentcatalog.service.spring.config;

import com.moneysupermarket.componentcatalog.service.partialresponse.PartialResponseHandlerInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class CustomWebMvcConfigurer implements WebMvcConfigurer {

    private final PartialResponseHandlerInterceptor partialResponseHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(partialResponseHandlerInterceptor);
    }
}
