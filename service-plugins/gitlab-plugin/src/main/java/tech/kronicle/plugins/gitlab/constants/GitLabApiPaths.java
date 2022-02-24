package tech.kronicle.plugins.gitlab.constants;

public final class GitLabApiPaths {

  public static final String PROJECTS = "/api/v4/projects";
  public static final String GROUP_PROJECTS = "/api/v4/groups/{groupPath}/projects";
  public static final String USER_PROJECTS = "/api/v4/users/{username}/projects";
  public static final String PROJECT_KRONICLE_YAML_FILE = "/api/v4/projects/{projectId}/repository/files/{kronicleMetadataFilePath}?ref={defaultBranch}";

  private GitLabApiPaths() {
  }
}
