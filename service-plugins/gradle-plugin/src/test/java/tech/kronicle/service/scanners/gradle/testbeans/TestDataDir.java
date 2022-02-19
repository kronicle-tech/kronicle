package tech.kronicle.service.scanners.gradle.testbeans;

import lombok.Value;

@Value
public class TestDataDir {

    String value;

    public TestDataDir(String testName) {
        this.value = "build/test-data/" + testName;
    }
}
