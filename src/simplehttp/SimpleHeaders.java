package simplehttp;

import com.sun.net.httpserver.Headers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents HTTP request and response headers as key-value pairs.
 */
public class SimpleHeaders {

    private final Map<String, List<String>> headers;

    private SimpleHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    // Converts from native Headers class.
    SimpleHeaders(Headers nativeHeaders) {
        this.headers = nativeHeaders
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Returns the list of values associated with the given header.
     */
    public List<String> get(String header) {
        return headers.get(header);
    }

    /**
     * Returns a singular value for the given header.
     * @param header the name of the header.
     * @return the singular value associated with the header.
     * @throws IllegalArgumentException if more or fewer than 1 values are
     * associated with the given header.
     */
    public String getOnly(String header) {
        List<String> headerValue = get(header);
        int numValues = headerValue.size();
        if (numValues != 1) {
            String msg =
                    "Expected 1 value for header " +
                            header + " but found " + numValues + ".";
            throw new IllegalArgumentException(header);
        }
        return headerValue.get(0);
    }

    /**
     * Returns true if a value is associated with the header.
     */
    public boolean containsHeader(String header) {
        return headers.containsKey(header);
    }

    /**
     * Returns true if there is a header with the given list of values.
     */
    public boolean containsValue(List<String> value) {
        return headers.containsValue(value);
    }

    /**
     * Returns true if there is a header with the given value.
     */
    public boolean containsValue(String value) {
        List<String> valueList = List.of(value);
        return containsValue(valueList);
    }

    /**
     * Returns the total number of headers.
     */
    public int size() {
        return headers.size();
    }

    /**
     * Converts this header into its builder form.
     */
    public Builder toBuilder() {
        return new Builder(headers);
    }

    /**
     * Converts this header into a map.
     */
    public Map<String, List<String>> toMap() {
        return headers;
    }

    /**
     * Creates a new {@link SimpleHeaders} instance with no headers.
     */
    public static SimpleHeaders createEmpty() {
        return new Builder().build();
    }

    /**
     * Builder for creating a new {@link SimpleHeaders}.
     */
    public static class Builder {

        private final Map<String, List<String>> headers;

        public Builder() {
            headers = new HashMap<>();
        }

        private Builder(Map<String, List<String>> headers) {
            this.headers = headers;
        }

        /**
         * Adds a new value to the header.
         */
        public Builder add(String header, String value) {
            List<String> headerValues = headers.getOrDefault(header,
                    new ArrayList<>());
            headerValues.add(value);
            headers.put(header, headerValues);
            return this;
        }

        /**
         * Adds all the given values to the header.
         */
        public Builder addAll(String header, List<String> values) {
            List<String> headerValues = headers.getOrDefault(header,
                    new ArrayList<>());
            headerValues.addAll(values);
            headers.put(header, headerValues);
            return this;
        }

        /**
         * Sets the given value as the singular value for the header.
         */
        public Builder set(String header, String value) {
            headers.put(header, List.of(value));
            return this;
        }

        /**
         * Removes any values associated with the header.
         */
        public Builder remove(String header) {
            headers.remove(header);
            return this;
        }

        /**
         * Builds a new {@link SimpleHeaders} from this builder.
         */
        public SimpleHeaders build() {
            return new SimpleHeaders(headers);
        }
    }
}
