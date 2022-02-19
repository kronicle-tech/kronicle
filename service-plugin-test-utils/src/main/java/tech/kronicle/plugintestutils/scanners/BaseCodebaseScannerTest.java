package tech.kronicle.plugintestutils.scanners;

import tech.kronicle.sdk.models.Repo;

import java.nio.file.Path;

public class BaseCodebaseScannerTest extends BaseScannerTest {

    private static final Repo TEST_REPO = new Repo("test");

    protected Repo getTestRepo() {
        return TEST_REPO;
    }

    protected Path getCodebaseDir(String name) {
        return getResourcesDir(name);
    }
}
