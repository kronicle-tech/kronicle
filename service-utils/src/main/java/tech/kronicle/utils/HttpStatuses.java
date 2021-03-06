package tech.kronicle.utils;

public final class HttpStatuses {

    public static final int OK = 200;
    public static final int MOVED_PERMANENTLY = 301;
    public static final int FOUND = 302;
    public static final int SEE_OTHER = 303;
    public static final int NOT_MODIFIED = 304;
    public static final int NOT_FOUND = 404;
    public static final int FORBIDDEN = 403;
    public static final int INTERNAL_SERVER_ERROR = 500;

    private HttpStatuses() {
    }
}
