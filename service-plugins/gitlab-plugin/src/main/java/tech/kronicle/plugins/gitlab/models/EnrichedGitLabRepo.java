package tech.kronicle.plugins.gitlab.models;

import lombok.Builder;
import lombok.Value;
import tech.kronicle.plugins.gitlab.config.GitLabAccessTokenConfig;
import tech.kronicle.plugins.gitlab.models.api.GitLabRepo;

@Value
@Builder
public class EnrichedGitLabRepo {

    GitLabRepo repo;
    String baseUrl;
    GitLabAccessTokenConfig accessToken;
    Boolean hasComponentMetadataFile;
}
