package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.kronicle.service.tests.ComponentTest;
import tech.kronicle.service.tests.Test;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestFinder {
    
    private final List<Test<?>> tests;

    public List<Test<?>> getAllTests() {
        return List.copyOf(tests);
    }

    public List<ComponentTest> getComponentTests() {
        return getTests(ComponentTest.class);
    }

    private <T extends Test<?>> List<T> getTests(Class<T> clazz) {
        return tests.stream()
                .filter(test -> clazz.isAssignableFrom(test.getClass()))
                .map(test -> (T) test)
                .collect(Collectors.toList());
    }

    public Test<?> getTest(String testId) {
        return tests.stream()
                .filter(test -> Objects.equals(test.id(), testId))
                .findFirst().orElse(null);
    }
}
