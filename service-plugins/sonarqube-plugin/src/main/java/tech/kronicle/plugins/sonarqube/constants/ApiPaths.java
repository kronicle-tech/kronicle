package tech.kronicle.plugins.sonarqube.constants;

public final class ApiPaths {

    public static final String BASE_PATH = "/api";
    public static final String SEARCH_COMPONENTS = BASE_PATH + "/components/search";
    public static final String SEARCH_METRICS = BASE_PATH + "/metrics/search";
    public static final String GET_COMPONENT_MEASURES = BASE_PATH + "/measures/component";

    private ApiPaths() {
    }
}
