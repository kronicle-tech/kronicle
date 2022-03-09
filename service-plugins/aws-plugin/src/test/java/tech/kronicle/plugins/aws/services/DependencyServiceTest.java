package tech.kronicle.plugins.aws.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.plugins.aws.xray.models.Service;
import tech.kronicle.plugins.aws.xray.services.DependencyAssembler;
import tech.kronicle.plugins.aws.xray.services.DependencyService;
import tech.kronicle.plugins.aws.xray.services.XRayServiceGraphFetcher;
import tech.kronicle.sdk.models.Dependency;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DependencyServiceTest {

    public DependencyService underTest;
    @Mock
    public XRayServiceGraphFetcher fetcher;
    @Mock
    public DependencyAssembler assembler;

    @BeforeEach
    public void beforeEach() {
        underTest = new DependencyService(fetcher, assembler);
    }

    @Test
    public void getDependenciesShouldFetchAServiceGraphAndAssembleDependencies() {
        // Given
        List<Service> services = List.of(
                new Service("test-service-1", null, null),
                new Service("test-service-2", null, null)
        );
        when(fetcher.getServiceGraph()).thenReturn(services);
        List<Dependency> dependencies = List.of(
                Dependency.builder()
                        .sourceComponentId("test-source-component-id-1")
                        .targetComponentId("test-target-component-id-2")
                        .build(),
                Dependency.builder()
                        .sourceComponentId("test-source-component-id-3")
                        .targetComponentId("test-target-component-id-4")
                        .build()
        );
        when(assembler.assembleDependencies(services)).thenReturn(dependencies);

        // When
        List<Dependency> returnValue = underTest.getDependencies();

        // Then
        assertThat(returnValue).isEqualTo(dependencies);
    }
}
