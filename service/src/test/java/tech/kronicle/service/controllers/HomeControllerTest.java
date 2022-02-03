package tech.kronicle.service.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.GetHomeResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class HomeControllerTest {

    private HomeController underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new HomeController();
    }

    @Test
    public void getHomeShouldReturnHome() {
        // When
        GetHomeResponse returnValue = underTest.getHome();

        // Then
        assertThat(returnValue).isNotNull();
    }
}
