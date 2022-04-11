package tech.kronicle.plugins.gitlab.models.api;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class GitLabPipeline {

    Long id;
    Integer iid;
    Long project_id;
    String sha;
    String ref;
    String status;
    String source;
    LocalDateTime created_at;
    LocalDateTime updated_at;
    String web_url;
}
