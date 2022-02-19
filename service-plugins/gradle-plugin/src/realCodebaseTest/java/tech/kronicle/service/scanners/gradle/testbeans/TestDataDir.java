package tech.kronicle.service.scanners.gradle.testbeans;

import lombok.Value;

@Value
public class TestDataDir {

    String value;

    public TestDataDir(Class<?> testClass) {
        this.value = "build/test-data/" + testClass.getName();
    }

    public TestDataDir(String testClassName) {
        this.value = "build/test-data/" + testClassName;
    }
}
