package com.moneysupermarket.componentcatalog.service.models;

import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RepoDirAndGitTest {

    @Mock
    private Git git;

    @Test
    public void closeShouldCloseGit() {
        // Given
        RepoDirAndGit underTest = new RepoDirAndGit(null, git);
        verify(git, never()).close();

        // When
        underTest.close();

        // Then
        verify(git).close();
    }
}
