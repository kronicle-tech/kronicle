package tech.kronicle.pluginapi.finders;

import tech.kronicle.common.utils.CaseUtils;
import tech.kronicle.componentmetadata.models.ComponentMetadata;
import tech.kronicle.pluginapi.ExtensionPointWithId;

import java.util.List;

public abstract class Finder<T> implements ExtensionPointWithId {

    public String id() {
        return CaseUtils.toKebabCase(getClass().getSimpleName()).replaceFirst("-finder$", "");
    }

    public abstract String description();

    public String notes() {
        return null;
    }

    public abstract List<T> find(ComponentMetadata componentMetadata);
}
