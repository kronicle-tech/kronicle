package tech.kronicle.plugins.zipkin.spring;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.function.client.WebClient;
import tech.kronicle.plugins.zipkin.PluginPackage;
import tech.kronicle.plugins.zipkin.client.ZipkinClientException;
import tech.kronicle.pluginutils.services.MapComparator;
import tech.kronicle.sdk.models.SummaryComponentDependencyNode;
import tech.kronicle.sdk.models.SummarySubComponentDependencyNode;

import java.time.Clock;
import java.time.Duration;
import java.util.Comparator;

@ComponentScan(basePackageClasses = PluginPackage.class)
public class SpringConfiguration {

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

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public RetryRegistry retryRegistry() {
        return RetryRegistry.custom()
                .addRetryConfig("zipkin-client", RetryConfig.custom()
                        .maxAttempts(10)
                        .intervalFunction(IntervalFunction.ofExponentialBackoff(Duration.ofSeconds(10), 2))
                        .retryExceptions(ZipkinClientException.class)
                        .build())
                .build();
    }
}
