package tech.kronicle.service;

import tech.kronicle.service.testutils.TestFileHelper;

import java.nio.file.Path;

public class BaseTest {

    public Path getResourcesDir(String name) {
        return TestFileHelper.getResourcesDir(name, getClass());
    }
}
