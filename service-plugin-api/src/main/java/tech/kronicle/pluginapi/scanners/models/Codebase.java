package tech.kronicle.pluginapi.scanners.models;

import tech.kronicle.sdk.models.ObjectWithReference;
import tech.kronicle.sdk.models.Repo;
import lombok.Value;

import java.nio.file.Path;

@Value
public class Codebase implements ObjectWithReference {

    Repo repo;
    Path dir;

    @Override
    public String reference() {
        return repo.reference();
    }
}
