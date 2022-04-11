package tech.kronicle.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ComponentMetadataTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        ComponentMetadata returnValue = new ObjectMapper().readValue(json, ComponentMetadata.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void constructorShouldMakeReposAnUnmodifiableList() {
        // Given
        ComponentMetadata underTest = ComponentMetadata.builder().repos(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getRepos().add(Repo.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeComponentTypesAnUnmodifiableList() {
        // Given
        ComponentMetadata underTest = ComponentMetadata.builder().componentTypes(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getComponentTypes().add(ComponentType.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakePlatformsAnUnmodifiableList() {
        // Given
        ComponentMetadata underTest = ComponentMetadata.builder().platforms(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getPlatforms().add(Platform.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeAreasAnUnmodifiableList() {
        // Given
        ComponentMetadata underTest = ComponentMetadata.builder().areas(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getAreas().add(Area.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeTeamsAnUnmodifiableList() {
        // Given
        ComponentMetadata underTest = ComponentMetadata.builder().teams(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTeams().add(Team.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeComponentsAnUnmodifiableList() {
        // Given
        ComponentMetadata underTest = ComponentMetadata.builder().components(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getComponents().add(Component.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
