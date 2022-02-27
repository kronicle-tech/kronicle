package tech.kronicle.service.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.RepoFinder;
import tech.kronicle.pluginapi.finders.models.ApiRepo;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RepoFinderServiceTest {

    private static final ApiRepo TEST_REPO_1 = createTestRepo(1);
    private static final ApiRepo TEST_REPO_2 = createTestRepo(2);
    private static final ApiRepo TEST_REPO_3 = createTestRepo(3);
    private static final ApiRepo TEST_REPO_4 = createTestRepo(4);

    private RepoFinderService underTest;
    @Mock
    private FinderExtensionRegistry mockFinderExtensionRegistry;
    @Mock
    private RepoFilterService mockRepoFilterService;
    @Mock
    private RepoFinder mockRepoFinder1;
    @Mock
    private RepoFinder mockRepoFinder2;

    @Test
    public void getRepoProvidersShouldUseTheRepoFindersToFindRepos() {
        // Given
        when(mockFinderExtensionRegistry.getRepoFinders()).thenReturn(List.of(mockRepoFinder1, mockRepoFinder2));
        when(mockRepoFinder1.find(null)).thenReturn(List.of(TEST_REPO_1, TEST_REPO_2));
        when(mockRepoFinder2.find(null)).thenReturn(List.of(TEST_REPO_3, TEST_REPO_4));
        when(mockRepoFilterService.keepRepo(any())).thenReturn(true);
        underTest = new RepoFinderService(mockFinderExtensionRegistry, mockRepoFilterService);

        // When
        List<ApiRepo> returnValue = underTest.findRepos();

        // Then
        assertThat(returnValue).containsExactly(TEST_REPO_1, TEST_REPO_2, TEST_REPO_3, TEST_REPO_4);
    }

    @Test
    public void getRepoProvidersShouldDeduplicateIdenticalReposFromDifferentRepoFinders() {
        // Given
        when(mockFinderExtensionRegistry.getRepoFinders()).thenReturn(List.of(mockRepoFinder1, mockRepoFinder2));
        when(mockRepoFinder1.find(null)).thenReturn(List.of(TEST_REPO_1));
        when(mockRepoFinder2.find(null)).thenReturn(List.of(TEST_REPO_1));
        when(mockRepoFilterService.keepRepo(any())).thenReturn(true);
        underTest = new RepoFinderService(mockFinderExtensionRegistry, mockRepoFilterService);

        // When
        List<ApiRepo> returnValue = underTest.findRepos();

        // Then
        assertThat(returnValue).containsExactly(TEST_REPO_1);
    }

    @Test
    public void getRepoProvidersShouldDeduplicateIdenticalReposFromTheSameRepoFinder() {
        // Given
        when(mockFinderExtensionRegistry.getRepoFinders()).thenReturn(List.of(mockRepoFinder1));
        when(mockRepoFinder1.find(null)).thenReturn(List.of(TEST_REPO_1, TEST_REPO_1));
        when(mockRepoFilterService.keepRepo(any())).thenReturn(true);
        underTest = new RepoFinderService(mockFinderExtensionRegistry, mockRepoFilterService);

        // When
        List<ApiRepo> returnValue = underTest.findRepos();

        // Then
        assertThat(returnValue).containsExactly(TEST_REPO_1);
    }

    @Test
    public void getRepoProvidersShouldUseRepoFilterServiceToFilterTheRepos() {
        // Given
        when(mockFinderExtensionRegistry.getRepoFinders()).thenReturn(List.of(mockRepoFinder1));
        when(mockRepoFinder1.find(null)).thenReturn(List.of(TEST_REPO_1, TEST_REPO_2, TEST_REPO_3));
        when(mockRepoFilterService.keepRepo(TEST_REPO_1)).thenReturn(true);
        when(mockRepoFilterService.keepRepo(TEST_REPO_2)).thenReturn(false);
        when(mockRepoFilterService.keepRepo(TEST_REPO_3)).thenReturn(true);
        underTest = new RepoFinderService(mockFinderExtensionRegistry, mockRepoFilterService);

        // When
        List<ApiRepo> returnValue = underTest.findRepos();

        // Then
        // Test repo 2 should be filtered out
        assertThat(returnValue).containsExactly(TEST_REPO_1, TEST_REPO_3);
    }

    private static ApiRepo createTestRepo(int repoNumber) {
        return new ApiRepo("https://example.com/test-repo-" + repoNumber, isOddNumber(repoNumber));
    }

    private static Boolean isOddNumber(int value) {
        return value % 2 == 1;
    }
}
