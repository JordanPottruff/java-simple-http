package simplehttp;

/**
 * Defines different header names as string constants.
 */
public final class HttpHeader {

    public static final String ACCESS_CONTROL_ALLOW_ORIGIN =
            "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_ALLOW_METHODS =
            "Access-Control-Allow-Methods";
    public static final String ACCEPT = "Accept";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LANGUAGE = "Content-Language";

    private HttpHeader() { /* Utility class not meant to be instantiated */ }
}
