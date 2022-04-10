package tech.kronicle.plugintestutils.scanners;

import tech.kronicle.sdk.models.RepoReference;

import java.nio.file.Path;

public class BaseCodebaseScannerTest extends BaseScannerTest {

    private static final RepoReference TEST_REPO = new RepoReference("test");

    protected RepoReference getTestRepo() {
        return TEST_REPO;
    }

    protected Path getCodebaseDir(String name) {
        return getResourcesDir(name);
    }
}
