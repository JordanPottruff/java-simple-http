package simplehttp;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Represents HTTP response data.
 */
public class SimpleResponse {

    private final SimpleHeaders headers;
    private final byte[] body;
    private final int statusCode;

    SimpleResponse(SimpleHeaders headers, byte[] body, int statusCode) {
        this.headers = headers;
        this.body = body;
        this.statusCode = statusCode;
    }

    /**
     * Returns the response headers.
     */
    public SimpleHeaders getHeaders() {
        return this.headers;
    }

    /**
     * Returns the response body as raw bytes.
     */
    public byte[] getBodyRaw() {
        return this.body;
    }

    /**
     * Returns the response body as a string, converted from the raw bytes.
     */
    public String getBodyString() {
        return new String(body, StandardCharsets.UTF_8);
    }

    /**
     * Returns the status code of the response.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Builder for creating a new {@link SimpleResponse}.
     */
    public static class Builder {

        private SimpleHeaders headers = SimpleHeaders.createEmpty();
        private byte[] body = new byte[0];
        private int statusCode;

        /**
         * Sets the headers for the response.
         */
        public Builder setHeaders(SimpleHeaders headers) {
            this.headers = headers;
            return this;
        }

        /**
         * Sets the body of the response to the given raw bytes.
         */
        public Builder setBody(byte[] body) {
            this.body = body;
            return this;
        }

        /**
         * Sets the body of the response to the bytes corresponding to the
         * given string.
         */
        public Builder setBody(String body) {
            return setBody(body, StandardCharsets.UTF_8);
        }

        /**
         * Sets the body of the response to the bytes corresponding to the
         * given string, using the given character set.
         */
        public Builder setBody(String body, Charset charset) {
            this.body = body.getBytes(charset);
            return this;
        }

        /**
         * Sets the status of the response to the given status code.
         */
        public Builder setStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        /**
         * Sets hte status of the response.
         */
        public Builder setStatusCode(HttpStatus status) {
            this.statusCode = status.code();
            return this;
        }

        /**
         * Builds a new {@link SimpleResponse} from this builder.
         */
        public SimpleResponse build() {
            return new SimpleResponse(headers, body, statusCode);
        }
    }
}
