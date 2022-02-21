package tech.kronicle.service.extensions;

import org.pf4j.ExtensionPoint;

import java.nio.file.Path;

public interface GitCloner extends ExtensionPoint {

    Path cloneOrPullRepo(String repoUrl);

    Path cloneOrPullRepo(String repoUrl, String repoRef);

}
