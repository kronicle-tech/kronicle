package tech.kronicle.pluginapi.finders;

import tech.kronicle.common.CaseUtils;
import tech.kronicle.pluginapi.ExtensionPointWithId;

import java.util.List;

public abstract class Finder<I, O> implements ExtensionPointWithId {

    public String id() {
        return CaseUtils.toKebabCase(getClass().getSimpleName()).replaceFirst("-finder$", "");
    }

    public abstract String description();

    public String notes() {
        return null;
    }

    public abstract List<O> find(I input);
}
