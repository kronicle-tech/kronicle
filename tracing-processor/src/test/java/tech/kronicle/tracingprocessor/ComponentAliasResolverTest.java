package tech.kronicle.tracingprocessor;

import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.finders.models.GenericSpan;
import tech.kronicle.pluginapi.finders.models.GenericTrace;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.sdk.constants.DependencyTypeIds;
import tech.kronicle.sdk.models.Dependency;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentAliasResolverTest {

    private final ComponentAliasResolver underTest = new ComponentAliasResolver();

    @Test
    public void tracingDataListShouldResolveAliasesInDependencySourceComponentIds() {
        // Given
        List<TracingData> tracingDataList = List.of(
                TracingData.builder()
                        .dependencies(List.of(
                                createDependency("test-source-1", "test-target-1"),
                                createDependency("test-source-2", "test-target-2")
                        ))
                        .build(),
                TracingData.builder()
                        .dependencies(List.of(
                                createDependency("test-source-3", "test-target-3"),
                                createDependency("test-source-4", "test-target-4")
                        ))
                        .build()
        );
        Map<String, String> componentAliasMap = Map.ofEntries(
                Map.entry("test-source-1", "test-source-1-real"),
                Map.entry("test-target-2", "test-target-2-real"),
                Map.entry("test-source-3", "test-source-3-real"),
                Map.entry("test-target-4", "test-target-4-real")
        );

        // When
        List<TracingData> returnValue = underTest.tracingDataList(tracingDataList, componentAliasMap);

        // Then
        assertThat(returnValue).isEqualTo(List.of(
                TracingData.builder()
                        .dependencies(List.of(
                                createDependency("test-source-1-real", "test-target-1"),
                                createDependency("test-source-2", "test-target-2-real")
                        ))
                        .build(),
                TracingData.builder()
                        .dependencies(List.of(
                                createDependency("test-source-3-real", "test-target-3"),
                                createDependency("test-source-4", "test-target-4-real")
                        ))
                        .build()
        ));
    }

    @Test
    public void tracingDataListShouldResolveAliasesInTraceSpanSourceNames() {
        // Given
        List<TracingData> tracingDataList = List.of(
                TracingData.builder()
                        .traces(List.of(
                                new GenericTrace(List.of(
                                        GenericSpan.builder()
                                                .sourceName("test-source-1")
                                                .name("test-name-1")
                                                .build(),
                                        GenericSpan.builder()
                                                .sourceName("test-source-2")
                                                .name("test-name-2")
                                                .build()
                                )),
                                new GenericTrace(List.of(
                                        GenericSpan.builder()
                                                .sourceName("test-source-3")
                                                .name("test-name-3")
                                                .build(),
                                        GenericSpan.builder()
                                                .sourceName("test-source-4")
                                                .name("test-name-4")
                                                .build()
                                ))
                        ))
                        .build(),
                TracingData.builder()
                        .traces(List.of(
                                new GenericTrace(List.of(
                                        GenericSpan.builder()
                                                .sourceName("test-source-5")
                                                .name("test-name-5")
                                                .build(),
                                        GenericSpan.builder()
                                                .sourceName("test-source-6")
                                                .name("test-name-6")
                                                .build()
                                )),
                                new GenericTrace(List.of(
                                        GenericSpan.builder()
                                                .sourceName("test-source-7")
                                                .name("test-name-7")
                                                .build(),
                                        GenericSpan.builder()
                                                .sourceName("test-source-8")
                                                .name("test-name-8")
                                                .build()
                                ))
                        ))
                        .build()
        );
        Map<String, String> componentAliasMap = Map.ofEntries(
                Map.entry("test-source-1", "test-source-1-real"),
                Map.entry("test-source-4", "test-source-4-real"),
                Map.entry("test-source-5", "test-source-5-real"),
                Map.entry("test-source-8", "test-source-8-real")
        );

        // When
        List<TracingData> returnValue = underTest.tracingDataList(tracingDataList, componentAliasMap);

        // Then
        assertThat(returnValue).isEqualTo(List.of(
                TracingData.builder()
                        .traces(List.of(
                                new GenericTrace(List.of(
                                        GenericSpan.builder()
                                                .sourceName("test-source-1-real")
                                                .name("test-name-1")
                                                .build(),
                                        GenericSpan.builder()
                                                .sourceName("test-source-2")
                                                .name("test-name-2")
                                                .build()
                                )),
                                new GenericTrace(List.of(
                                        GenericSpan.builder()
                                                .sourceName("test-source-3")
                                                .name("test-name-3")
                                                .build(),
                                        GenericSpan.builder()
                                                .sourceName("test-source-4-real")
                                                .name("test-name-4")
                                                .build()
                                ))
                        ))
                        .build(),
                TracingData.builder()
                        .traces(List.of(
                                new GenericTrace(List.of(
                                        GenericSpan.builder()
                                                .sourceName("test-source-5-real")
                                                .name("test-name-5")
                                                .build(),
                                        GenericSpan.builder()
                                                .sourceName("test-source-6")
                                                .name("test-name-6")
                                                .build()
                                )),
                                new GenericTrace(List.of(
                                        GenericSpan.builder()
                                                .sourceName("test-source-7")
                                                .name("test-name-7")
                                                .build(),
                                        GenericSpan.builder()
                                                .sourceName("test-source-8-real")
                                                .name("test-name-8")
                                                .build()
                                ))
                        ))
                        .build()
        ));
    }

    private Dependency createDependency(String sourceComponentId, String targetComponentId) {
        return new Dependency(
                sourceComponentId,
                targetComponentId,
                DependencyTypeIds.TRACE,
                null,
                null
        );
    }
}
