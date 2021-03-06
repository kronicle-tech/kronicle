package tech.kronicle.service.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.RepoFinder;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.utils.ThrowableToScannerErrorMapper;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RepoFinderServiceTest {

    private static final Duration CACHE_TTL = Duration.ofHours(1);
    private static final Repo TEST_REPO_1 = createTestRepo(1);
    private static final Repo TEST_REPO_2 = createTestRepo(2);
    private static final Repo TEST_REPO_3 = createTestRepo(3);
    private static final Repo TEST_REPO_4 = createTestRepo(4);

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
        when(mockRepoFinder1.find(null)).thenReturn(Output.ofOutput(List.of(TEST_REPO_1, TEST_REPO_2), CACHE_TTL));
        when(mockRepoFinder2.find(null)).thenReturn(Output.ofOutput(List.of(TEST_REPO_3, TEST_REPO_4), CACHE_TTL));
        when(mockRepoFilterService.keepRepo(any())).thenReturn(true);
        underTest = createUnderTest();

        // When
        List<Repo> returnValue = underTest.findRepos();

        // Then
        assertThat(returnValue).containsExactly(TEST_REPO_1, TEST_REPO_2, TEST_REPO_3, TEST_REPO_4);
    }

    @Test
    public void getRepoProvidersShouldDeduplicateIdenticalReposFromDifferentRepoFinders() {
        // Given
        when(mockFinderExtensionRegistry.getRepoFinders()).thenReturn(List.of(mockRepoFinder1, mockRepoFinder2));
        when(mockRepoFinder1.find(null)).thenReturn(Output.ofOutput(List.of(TEST_REPO_1), CACHE_TTL));
        when(mockRepoFinder2.find(null)).thenReturn(Output.ofOutput(List.of(TEST_REPO_1), CACHE_TTL));
        when(mockRepoFilterService.keepRepo(any())).thenReturn(true);
        underTest = createUnderTest();

        // When
        List<Repo> returnValue = underTest.findRepos();

        // Then
        assertThat(returnValue).containsExactly(TEST_REPO_1);
    }

    @Test
    public void getRepoProvidersShouldDeduplicateIdenticalReposFromTheSameRepoFinder() {
        // Given
        when(mockFinderExtensionRegistry.getRepoFinders()).thenReturn(List.of(mockRepoFinder1));
        when(mockRepoFinder1.find(null)).thenReturn(Output.ofOutput(List.of(TEST_REPO_1, TEST_REPO_1), CACHE_TTL));
        when(mockRepoFilterService.keepRepo(any())).thenReturn(true);
        underTest = createUnderTest();

        // When
        List<Repo> returnValue = underTest.findRepos();

        // Then
        assertThat(returnValue).containsExactly(TEST_REPO_1);
    }

    @Test
    public void getRepoProvidersShouldUseRepoFilterServiceToFilterTheRepos() {
        // Given
        when(mockFinderExtensionRegistry.getRepoFinders()).thenReturn(List.of(mockRepoFinder1));
        when(mockRepoFinder1.find(null)).thenReturn(Output.ofOutput(List.of(TEST_REPO_1, TEST_REPO_2, TEST_REPO_3), CACHE_TTL));
        when(mockRepoFilterService.keepRepo(TEST_REPO_1)).thenReturn(true);
        when(mockRepoFilterService.keepRepo(TEST_REPO_2)).thenReturn(false);
        when(mockRepoFilterService.keepRepo(TEST_REPO_3)).thenReturn(true);
        underTest = createUnderTest();

        // When
        List<Repo> returnValue = underTest.findRepos();

        // Then
        // Test repo 2 should be filtered out
        assertThat(returnValue).containsExactly(TEST_REPO_1, TEST_REPO_3);
    }

    private RepoFinderService createUnderTest() {
        return new RepoFinderService(
                mockFinderExtensionRegistry,
                new ExtensionExecutor(
                        new ExtensionOutputCache(
                                new ExtensionOutputCacheLoader(),
                                new ExtensionOutputCacheExpiry()
                        ),
                        new ThrowableToScannerErrorMapper()
                ),
                mockRepoFilterService
        );
    }

    private static Repo createTestRepo(int repoNumber) {
        return Repo.builder()
                .url("https://example.com/test-repo-" + repoNumber)
                .hasComponentMetadataFile(isOddNumber(repoNumber))
                .build();
    }

    private static Boolean isOddNumber(int value) {
        return value % 2 == 1;
    }
}
