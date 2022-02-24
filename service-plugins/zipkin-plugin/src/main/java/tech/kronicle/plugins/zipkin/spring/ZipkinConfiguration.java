package tech.kronicle.plugins.zipkin.spring;

import tech.kronicle.sdk.models.SummaryComponentDependencyNode;
import tech.kronicle.sdk.models.SummarySubComponentDependencyNode;
import tech.kronicle.pluginutils.services.MapComparator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;

@Configuration
public class ZipkinConfiguration {

    @Bean
    public MapComparator<String, String> mapComparator() {
        return new MapComparator<>();
    }

    @Bean
    public Comparator<SummaryComponentDependencyNode> componentNodeComparator() {
        return Comparator.comparing(SummaryComponentDependencyNode::getComponentId);
    }

    @Bean
    public Comparator<SummarySubComponentDependencyNode> subComponentNodeComparator(MapComparator<String, String> mapComparator) {
        return Comparator.comparing(SummarySubComponentDependencyNode::getComponentId)
                .thenComparing(SummarySubComponentDependencyNode::getSpanName, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(SummarySubComponentDependencyNode::getTags, mapComparator);
    }
}
