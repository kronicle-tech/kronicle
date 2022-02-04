package tech.kronicle.service.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HealthControllerTest {

    private HealthController underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new HealthController();
    }

    @Test
    public void getHealthShouldReturnHealth() {
        // When
        String returnValue = underTest.getHealth();

        // Then
        assertThat(returnValue).isEqualTo("OK");
    }
}
