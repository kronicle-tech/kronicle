package tech.kronicle.service.services;

import org.springframework.stereotype.Service;
import tech.kronicle.sdk.models.ComponentMetadata;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static tech.kronicle.utils.StreamUtils.distinctByKey;

@Service
public class ComponentAliasMapCreator {

    public Map<String, String> createComponentAliasMap(ComponentMetadata componentMetadata) {
        return componentMetadata.getComponents().stream()
                .flatMap(component -> {
                    String id = component.getId();
                    return Stream.concat(
                            Stream.of(Map.entry(id, id)),
                            Optional.ofNullable(component.getAliases())
                                    .orElse(List.of())
                                    .stream()
                                    .map(alias -> Map.entry(alias.getId(), id))
                    );
                })
                .filter(distinctByKey(Map.Entry::getKey))
                .collect(Collectors.toUnmodifiableMap(
                   Map.Entry::getKey,
                   Map.Entry::getValue
                ));
    }
}
