package tech.kronicle.plugintestutils;

import tech.kronicle.plugintestutils.testutils.TestFileHelper;

import java.nio.file.Path;

public class BaseTest {

    public Path getResourcesDir(String name) {
        return TestFileHelper.getResourcesDir(name, getClass());
    }
}
