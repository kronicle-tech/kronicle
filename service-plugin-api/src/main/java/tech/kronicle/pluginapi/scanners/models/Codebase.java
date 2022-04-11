package tech.kronicle.pluginapi.scanners.models;

import lombok.Value;
import tech.kronicle.sdk.models.ObjectWithReference;
import tech.kronicle.sdk.models.RepoReference;

import java.nio.file.Path;

@Value
public class Codebase implements ObjectWithReference {

    RepoReference repo;
    Path dir;

    @Override
    public String reference() {
        return repo.reference();
    }
}
