package tech.kronicle.service.scanners.zipkin.constants;

public final class ZipkinApiPaths {

    public static final String BASE_PATH = "/zipkin/api/v2";
    public static final String DEPENDENCIES = BASE_PATH + "/dependencies";
    public static final String SERVICES = BASE_PATH + "/services";
    public static final String SPANS = BASE_PATH + "/spans";
    public static final String TRACES = BASE_PATH + "/traces";

    private ZipkinApiPaths() {
    }
}
