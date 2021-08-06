package com.moneysupermarket.componentcatalog.service.scanners.zipkin.models.api;

import com.moneysupermarket.componentcatalog.service.scanners.zipkin.models.api.Service;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ServiceTest {

    @Test
    public void constructorShouldMakeAnnotationsAnUnmodifiableList() {
        // Given
        Service underTest = Service.builder().spanNames(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getSpanNames().add("test"));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
