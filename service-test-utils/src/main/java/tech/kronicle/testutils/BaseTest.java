package tech.kronicle.testutils;

import java.nio.file.Path;

public class BaseTest {

    public Path getResourcesDir(String name) {
        return TestFileHelper.getResourcesDir(name, getClass());
    }
}
