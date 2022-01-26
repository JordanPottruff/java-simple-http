package simplehttp;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents HTTP request data.
 */
public class SimpleRequest {

    private final SimpleHeaders headers;
    private final String body;
    private final URI uri;

    private SimpleRequest(SimpleHeaders headers,
                          String body,
                          URI uri) {
        this.headers = headers;
        this.body = body;
        this.uri = uri;
    }

    /**
     * Returns the headers in the request.
     */
    public SimpleHeaders getHeaders() {
        return headers;
    }

    /**
     * Returns the request body.
     */
    public String getBody() {
        return body;
    }

    /**
     * Returns the URI of the request.
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Returns the query parameters in the request.
     */
    public Map<String, String> getQueryParams() {
        Map<String, String> queryParams = new HashMap<>();
        String query = uri.getQuery();
        if (query == null || query.isEmpty()) {
            return new HashMap<>();
        }
        String[] pairs = query.split("&");
        for (String pair: pairs) {
            String[] keyValue = pair.split("=");
            Charset charset = StandardCharsets.UTF_8;
            String key = URLDecoder.decode(keyValue[0], charset);
            String value = URLDecoder.decode(keyValue[1], charset);
            queryParams.put(key, value);
        }
        return queryParams;
    }

    /**
     * Returns the value of the given query parameter from the request.
     * @param key the parameter name.
     * @return the value of the parameter.
     * @throws IllegalStateException if the query parameter is not defined.
     */
    public String getQueryParam(String key) {
        Map<String, String> queryParams = getQueryParams();
        if (!queryParams.containsKey(key)) {
            String msg = "Key " + key + " is not in query parameters "
                    + uri.getQuery();
            throw new IllegalArgumentException(msg);
        }
        return queryParams.get(key);
    }

    /**
     * Converts this request into a {@link Builder}.
     */
    public Builder toBuilder() {
        return new Builder(headers, body, uri);
    }

    static SimpleRequest fromExchange(HttpExchange exchange) throws IOException {
        String body = getRequestBody(exchange);
        SimpleHeaders headers =
                new SimpleHeaders(exchange.getRequestHeaders());
        return new SimpleRequest.Builder()
                .setBody(body)
                .setHeaders(headers)
                .setUri(exchange.getRequestURI())
                .build();
    }

    private static String getRequestBody(HttpExchange exchange) throws IOException {
        return new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    /**
     * Builder for creating a new {@link SimpleRequest}.
     */
    public static class Builder {

        private SimpleHeaders headers;
        private String body;
        private URI uri;

        public Builder() {}

        private Builder(SimpleHeaders headers, String body, URI uri) {
            this.headers = headers;
            this.body = body;
            this.uri = uri;
        }

        /**
         * Sets the headers for the request.
         */
        public Builder setHeaders(SimpleHeaders headers) {
            this.headers = headers;
            return this;
        }

        /**
         * Sets the body of the request.
         */
        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        /**
         * Sets the URI of the request.
         */
        public Builder setUri(URI uri) {
            this.uri = uri;
            return this;
        }

        /**
         * Builds a new {@link SimpleRequest} from this builder.
         */
        public SimpleRequest build() {
            return new SimpleRequest(headers, body, uri);
        }
    }
}
