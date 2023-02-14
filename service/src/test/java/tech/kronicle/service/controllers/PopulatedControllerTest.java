package tech.kronicle.service.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tech.kronicle.service.services.ComponentService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PopulatedControllerTest {

    @Mock
    private ComponentService mockComponentService;
    private PopulatedController underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new PopulatedController(mockComponentService);
    }

    @Test
    public void getHealthShouldReturnPopulatedWhenThereAreComponents() {
        // Given
        when(mockComponentService.hasComponents()).thenReturn(true);

        // When
        ResponseEntity<String> returnValue = underTest.getPopulated();

        // Then
        assertThat(returnValue.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(returnValue.getBody()).isEqualTo("Populated");
    }

    @Test
    public void getHealthShouldReturnNotPopulatedWhenThereAreNoComponents() {
        // Given
        when(mockComponentService.hasComponents()).thenReturn(false);

        // When
        ResponseEntity<String> returnValue = underTest.getPopulated();

        // Then
        assertThat(returnValue.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(returnValue.getBody()).isEqualTo("Not populated");
    }
}
