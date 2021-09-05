package tech.kronicle.service.services;

import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.TestOutcome;
import tech.kronicle.sdk.models.TestResult;
import tech.kronicle.service.tests.ComponentTest;
import tech.kronicle.service.tests.models.TestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.kronicle.common.utils.StringEscapeUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestEngine {

    private final TestFinder testFinder;

    public void test(ConcurrentHashMap<String, Component> componentMap) {
        TestContext testContext = new TestContext(componentMap);
        List<ComponentTest> componentTests = testFinder.getComponentTests();
        log.info("Executing {} component tests for {} components", componentTests.size(), componentMap.size());
        testContext.getComponentMap().values().forEach(executeTests(componentMap, testContext, componentTests));
    }

    private Consumer<Component> executeTests(ConcurrentHashMap<String, Component> componentMap, TestContext testContext, List<ComponentTest> tests) {
        return component -> {
            List<TestResult> results = tests.stream()
                    .map(executeTest(component, testContext))
                    .collect(Collectors.toList());
            componentMap.put(component.getId(), component.withTestResults(results));
        };
    }

    private Function<ComponentTest, TestResult> executeTest(Component component, TestContext testContext) {
        return test -> {
            log.info("Executing test {} for \"{}\"", test.id(), StringEscapeUtils.escapeString(component.reference()));
            try {
                return test.test(component, testContext);
            } catch (Exception e) {
                log.error("Failed to execute test {} for \"{}\"", test.id(), StringEscapeUtils.escapeString(component.reference()), e);
                return new TestResult(test.id(), TestOutcome.FAIL, test.priority(), String.format(
                        "Test generated an error:\n"
                                + "\n"
                                + "```\n"
                                + "%s\n"
                                + "```\n",
                        e.getMessage()));
            }
        };
    }
}
