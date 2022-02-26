package tech.kronicle.pluginapi.finders;

import tech.kronicle.common.utils.CaseUtils;
import tech.kronicle.pluginapi.ExtensionPointWithId;
import tech.kronicle.pluginapi.finders.models.ApiRepo;

import java.util.List;

public abstract class RepoFinder implements ExtensionPointWithId {

    public String id() {
        return CaseUtils.toKebabCase(getClass().getSimpleName()).replaceFirst("-repo-finder$", "");
    }

    public abstract List<ApiRepo> findApiRepos();
}
