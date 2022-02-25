package tech.kronicle.plugins.gradle;

import lombok.Value;

@Value
public class TestDataDir {

    String value;

    public TestDataDir(String testName) {
        this.value = "build/test-data/" + testName;
    }
}
