package tech.kronicle.plugins.aws.resourcegroupstaggingapi.services;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.plugins.aws.config.AwsProfileConfig;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.client.ResourceGroupsTaggingApiClientFacade;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.client.ResourceGroupsTaggingApiClientFacadeFactory;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResourcePage;

import java.util.List;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceFetcherTest {

    @Mock
    private ResourceGroupsTaggingApiClientFacadeFactory clientFacadeFactory;

    @Test
    public void getResourcesShouldUseTheClientFacadeToGetAllResourcesAndThenReturnThem() {
        // Given
        ResourceFetcher underTest = createUnderTest();
        AwsProfileAndRegion profileAndRegion = new AwsProfileAndRegion(
                new AwsProfileConfig(
                        "test-access-key-id",
                        "test-secret-access-key",
                        null,
                        null
                ),
                "test-region"
        );
        FakeResourceGroupsTaggingApiClientFacade clientFacade = new FakeResourceGroupsTaggingApiClientFacade(2);
        when(clientFacadeFactory.createResourceGroupsTaggingApiClientFacade(profileAndRegion)).thenReturn(clientFacade);

        // When
        List<ResourceGroupsTaggingApiResource> returnValue = underTest.getResources(profileAndRegion);

        // Then
        assertThat(returnValue).isEqualTo(List.of(
                new ResourceGroupsTaggingApiResource("test-arn-null-1", List.of()),
                new ResourceGroupsTaggingApiResource("test-arn-null-2", List.of()),
                new ResourceGroupsTaggingApiResource("test-arn-2-1", List.of()),
                new ResourceGroupsTaggingApiResource("test-arn-2-2", List.of())
        ));
        assertThat(clientFacade.isClosed).isTrue();
    }

    private ResourceFetcher createUnderTest() {
        return new ResourceFetcher(clientFacadeFactory);
    }

    @RequiredArgsConstructor
    private static class FakeResourceGroupsTaggingApiClientFacade implements ResourceGroupsTaggingApiClientFacade {

        private final int pageCount;
        private boolean isClosed;

        @Override
        public ResourceGroupsTaggingApiResourcePage getResources(String nextToken) {
            return new ResourceGroupsTaggingApiResourcePage(
                    List.of(
                            createResourceGroupsTaggingApiResource(nextToken, 1),
                            createResourceGroupsTaggingApiResource(nextToken, 2)
                    ),
                    getNextPage(nextToken)
            );
        }
        
        private ResourceGroupsTaggingApiResource createResourceGroupsTaggingApiResource(String nextToken, int resourceGroupsTaggingApiResourceNumber) {
            return new ResourceGroupsTaggingApiResource(
                    "test-arn-" + nextToken + "-" + resourceGroupsTaggingApiResourceNumber,
                    List.of()
            );
        }

        private String getNextPage(String nextToken) {
            int pageNumber = isNull(nextToken) ? 1 : Integer.parseInt(nextToken);
            int nextPageNumber = pageNumber + 1;

            if (nextPageNumber > pageCount) {
                return null;
            }

            return Integer.toString(nextPageNumber);
        }
        
        @Override
        public void close() {
            isClosed = true;
        }
    }
}
