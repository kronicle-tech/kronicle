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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.plugins.aws.testutils.DependencyUtils.createDependency;
import static tech.kronicle.plugins.aws.testutils.XRayDependencyUtils.createXrayDependency;

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
                createXrayDependency(1),
                createXrayDependency(2)
        );
        AwsProfileAndRegion profileAndRegion = new AwsProfileAndRegion(profile, "test-region-1");
        when(fetcher.getServiceGraph(profileAndRegion)).thenReturn(services);
        List<Dependency> dependencies = List.of(
                createDependency(1),
                createDependency(2)
        );
        when(assembler.assembleDependencies(services)).thenReturn(dependencies);

        // When
        List<Map.Entry<AwsProfileAndRegion, List<Dependency>>> returnValue = underTest.getDependencies();

        // Then
        assertThat(returnValue).isEqualTo(List.of(
                Map.entry(profileAndRegion, dependencies)
        ));
    }

    private AwsProfileConfig createProfile(List<String> regions) {
        return new AwsProfileConfig(
                null,
                null,
                null,
                regions,
                null, null);
    }

    @Test
    public void getDependenciesShouldFetchAServiceGraphForAProfileAndMultipleRegionsAndAssembleDependencies() {
        // Given
        AwsProfileConfig profile = createProfile(List.of("test-region-1", "test-region-2"));
        underTest = createUnderTest(List.of(profile));
        XRayDependency service1 = createXrayDependency(1);
        XRayDependency service2 = createXrayDependency(2);
        XRayDependency service3 = createXrayDependency(3);
        XRayDependency service4 = createXrayDependency(4);
        AwsProfileAndRegion profileAndRegion1 = new AwsProfileAndRegion(profile, "test-region-1");
        AwsProfileAndRegion profileAndRegion2 = new AwsProfileAndRegion(profile, "test-region-2");
        when(fetcher.getServiceGraph(profileAndRegion1)).thenReturn(List.of(
                service1,
                service2
        ));
        when(fetcher.getServiceGraph(profileAndRegion2)).thenReturn(List.of(
                service3,
                service4
        ));
        List<Dependency> dependencies1 = List.of(
                createDependency(1),
                createDependency(2)
        );
        List<Dependency> dependencies2 = List.of(
                createDependency(3),
                createDependency(4)
        );
        when(assembler.assembleDependencies(List.of(
                service1,
                service2
        ))).thenReturn(dependencies1);
        when(assembler.assembleDependencies(List.of(
                service3,
                service4
        ))).thenReturn(dependencies2);

        // When
        List<Map.Entry<AwsProfileAndRegion, List<Dependency>>> returnValue = underTest.getDependencies();

        // Then
        assertThat(returnValue).isEqualTo(List.of(
                Map.entry(profileAndRegion1, dependencies1),
                Map.entry(profileAndRegion2, dependencies2)
        ));
    }

    @Test
    public void getDependenciesShouldFetchAServiceGraphForMultipleProfilesAndMultipleRegionsAndAssembleDependencies() {
        // Given
        AwsProfileConfig profile1 = createProfile(List.of("test-region-1", "test-region-2"));
        AwsProfileConfig profile2 = createProfile(List.of("test-region-3", "test-region-4"));
        underTest = createUnderTest(List.of(profile1, profile2));
        XRayDependency service1 = createXrayDependency(1);
        XRayDependency service2 = createXrayDependency(2);
        XRayDependency service3 = createXrayDependency(3);
        XRayDependency service4 = createXrayDependency(4);
        XRayDependency service5 = createXrayDependency(5);
        XRayDependency service6 = createXrayDependency(6);
        XRayDependency service7 = createXrayDependency(7);
        XRayDependency service8 = createXrayDependency(8);
        AwsProfileAndRegion profileAndRegion1 = new AwsProfileAndRegion(profile1, "test-region-1");
        AwsProfileAndRegion profileAndRegion3 = new AwsProfileAndRegion(profile2, "test-region-3");
        AwsProfileAndRegion profileAndRegion4 = new AwsProfileAndRegion(profile2, "test-region-4");
        AwsProfileAndRegion profileAndRegion2 = new AwsProfileAndRegion(profile1, "test-region-2");
        when(fetcher.getServiceGraph(profileAndRegion1)).thenReturn(List.of(
                service1,
                service2
        ));
        when(fetcher.getServiceGraph(profileAndRegion2)).thenReturn(List.of(
                service3,
                service4
        ));
        when(fetcher.getServiceGraph(profileAndRegion3)).thenReturn(List.of(
                service5,
                service6
        ));
        when(fetcher.getServiceGraph(profileAndRegion4)).thenReturn(List.of(
                service7,
                service8
        ));
        List<Dependency> dependencies1 = List.of(
                createDependency(1),
                createDependency(2)
        );
        List<Dependency> dependencies2 = List.of(
                createDependency(3),
                createDependency(4)
        );
        List<Dependency> dependencies3 = List.of(
                createDependency(5),
                createDependency(6)
        );
        List<Dependency> dependencies4 = List.of(
                createDependency(7),
                createDependency(8)
        );
        when(assembler.assembleDependencies(List.of(
                service1,
                service2
        ))).thenReturn(dependencies1);
        when(assembler.assembleDependencies(List.of(
                service3,
                service4
        ))).thenReturn(dependencies2);
        when(assembler.assembleDependencies(List.of(
                service5,
                service6
        ))).thenReturn(dependencies3);
        when(assembler.assembleDependencies(List.of(
                service7,
                service8
        ))).thenReturn(dependencies4);

        // When
        List<Map.Entry<AwsProfileAndRegion, List<Dependency>>> returnValue = underTest.getDependencies();

        // Then
        assertThat(returnValue).isEqualTo(List.of(
                Map.entry(profileAndRegion1, dependencies1),
                Map.entry(profileAndRegion2, dependencies2),
                Map.entry(profileAndRegion3, dependencies3),
                Map.entry(profileAndRegion4, dependencies4)
        ));
    }


    private DependencyService createUnderTest(List<AwsProfileConfig> profiles) {
        return new DependencyService(
                fetcher,
                assembler,
                new AwsConfig(
                        profiles,
                        null,
                        null, null,
                        null, null,
                        null,
                        null
                )
        );
    }
}
