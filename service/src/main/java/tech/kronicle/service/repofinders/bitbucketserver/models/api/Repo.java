package tech.kronicle.service.repofinders.bitbucketserver.models.api;

import lombok.Value;

/**
 * This class only contains a subset of the fields returned by the Bitbucket Server API
 */
@Value
public class Repo {

    String slug;
    String name;
    String scmId;
    String state;
    Project project;
    Links links;
}
