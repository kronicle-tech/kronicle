package tech.kronicle.pluginapi.git;

import org.pf4j.ExtensionPoint;

import java.nio.file.Path;

public interface GitCloner extends ExtensionPoint {

    Path cloneOrPullRepo(String repoUrl);

    Path cloneOrPullRepo(String repoUrl, String repoRef);

}
