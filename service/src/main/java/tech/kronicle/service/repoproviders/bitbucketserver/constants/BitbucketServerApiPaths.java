package tech.kronicle.service.repoproviders.bitbucketserver.constants;

public final class BitbucketServerApiPaths {

    public static final String BASE_PATH = "/rest/api/1.0";
    public static final String REPOS = BASE_PATH + "/repos";
    public static final String BROWSE = BASE_PATH + "/projects/{projectKey}/repos/{repositorySlug}/browse";

    private BitbucketServerApiPaths() {
    }
}
