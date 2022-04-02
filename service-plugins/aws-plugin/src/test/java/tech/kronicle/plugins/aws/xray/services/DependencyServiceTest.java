package tech.kronicle.plugins.aws.xray.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.config.AwsProfileConfig;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.xray.models.XRayDependency;
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

    @Test
    public void getDependenciesShouldFetchAServiceGraphForAProfileAndRegionAndAssembleDependencies() {
        // Given
        AwsProfileConfig profile = createProfile(List.of("test-region-1"));
        underTest = createUnderTest(List.of(profile));
        List<XRayDependency> services = List.of(
                createDependency(1),
                createDependency(2)
        );
        when(fetcher.getServiceGraph(new AwsProfileAndRegion(profile, "test-region-1"))).thenReturn(services);
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

    private AwsProfileConfig createProfile(List<String> regions) {
        return new AwsProfileConfig(
                null,
                null,
                regions,
                null);
    }

    @Test
    public void getDependenciesShouldFetchAServiceGraphForAProfileAndMultipleRegionsAndAssembleDependencies() {
        // Given
        AwsProfileConfig profile = createProfile(List.of("test-region-1", "test-region-2"));
        underTest = createUnderTest(List.of(profile));
        XRayDependency service1 = createDependency(1);
        XRayDependency service2 = createDependency(2);
        XRayDependency service3 = createDependency(3);
        XRayDependency service4 = createDependency(4);
        when(fetcher.getServiceGraph(new AwsProfileAndRegion(profile, "test-region-1"))).thenReturn(List.of(
                service1,
                service2
        ));
        when(fetcher.getServiceGraph(new AwsProfileAndRegion(profile, "test-region-2"))).thenReturn(List.of(
                service3,
                service4
        ));
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
        when(assembler.assembleDependencies(List.of(
                service1,
                service2,
                service3,
                service4
        ))).thenReturn(dependencies);

        // When
        List<Dependency> returnValue = underTest.getDependencies();

        // Then
        assertThat(returnValue).isEqualTo(dependencies);
    }

    @Test
    public void getDependenciesShouldFetchAServiceGraphForMultipleProfilesAndMultipleRegionsAndAssembleDependencies() {
        // Given
        AwsProfileConfig profile1 = createProfile(List.of("test-region-1", "test-region-2"));
        AwsProfileConfig profile2 = createProfile(List.of("test-region-3", "test-region-4"));
        underTest = createUnderTest(List.of(profile1, profile2));
        XRayDependency dependency1 = createDependency(1);
        XRayDependency dependency2 = createDependency(2);
        XRayDependency dependency3 = createDependency(3);
        XRayDependency dependency4 = createDependency(4);
        XRayDependency dependency5 = createDependency(5);
        XRayDependency dependency6 = createDependency(6);
        XRayDependency dependency7 = createDependency(7);
        XRayDependency dependency8 = createDependency(8);
        when(fetcher.getServiceGraph(new AwsProfileAndRegion(profile1, "test-region-1"))).thenReturn(List.of(
                dependency1,
                dependency2
        ));
        when(fetcher.getServiceGraph(new AwsProfileAndRegion(profile1, "test-region-2"))).thenReturn(List.of(
                dependency3,
                dependency4
        ));
        when(fetcher.getServiceGraph(new AwsProfileAndRegion(profile2, "test-region-3"))).thenReturn(List.of(
                dependency5,
                dependency6
        ));
        when(fetcher.getServiceGraph(new AwsProfileAndRegion(profile2, "test-region-4"))).thenReturn(List.of(
                dependency7,
                dependency8
        ));
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
        when(assembler.assembleDependencies(List.of(
                dependency1,
                dependency2,
                dependency3,
                dependency4,
                dependency5,
                dependency6,
                dependency7,
                dependency8
        ))).thenReturn(dependencies);

        // When
        List<Dependency> returnValue = underTest.getDependencies();

        // Then
        assertThat(returnValue).isEqualTo(dependencies);
    }

    private XRayDependency createDependency(int dependencyNumber) {
        return new XRayDependency(
                List.of(
                        createServiceName(dependencyNumber, 1),
                        createServiceName(dependencyNumber, 2)
                ),
                List.of(
                        createServiceName(dependencyNumber, 3),
                        createServiceName(dependencyNumber, 4)
                )
        );
    }

    private String createServiceName(int dependencyNumber, int serviceNameNumber) {
        return "test-dependency-" + dependencyNumber + "-" + serviceNameNumber;
    }

    private DependencyService createUnderTest(List<AwsProfileConfig> profiles) {
        return new DependencyService(fetcher, assembler, new AwsConfig(profiles, null, null));
    }
}
