package tech.kronicle.sdk.models.git;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class GitRepoTest {

    @Test
    public void constructorShouldMakeAuthorsAnUnmodifiableList() {
        // Given
        GitRepo underTest = GitRepo.builder().authors(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getAuthors().add(Identity.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeCommittersAnUnmodifiableList() {
        // Given
        GitRepo underTest = GitRepo.builder().committers(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getCommitters().add(Identity.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
