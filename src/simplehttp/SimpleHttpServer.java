package simplehttp;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * A simplified HTTP server that wraps Java's native implementation. The
 * server can be created and configured using the {@link Builder} class,
 * including adding actions that define how HTTP requests should be handled.
 *
 * Similar to the native implementation, this server uses the main thread if
 * no executor is provided. In order to handle multiple requests in tandem, a
 * mutli-threaded executor will need to be provided. A backlog size can also be
 * configured to handle how many messages can be waiting to be handled.
 */
public class SimpleHttpServer {

    private final String hostname;
    private final Integer port;
    private final Integer backlog;
    private final Executor executor;

    private HttpServer server;
    private boolean running;
    private Set<SimpleAction> actions;

    private SimpleHttpServer(String hostname, int port, int backlog,
                             Executor executor, Set<SimpleAction> actions) {
        this.hostname = hostname;
        this.port = port;
        this.backlog = backlog;
        this.executor = executor;
        this.actions = actions;
    }

    /**
     * Starts the server and allows actions to begin handling requests.
     */
    public void start() {
        InetSocketAddress address = new InetSocketAddress(hostname, port);
        try {
            server = HttpServer.create(address, backlog);
            configureServer();
            server.start();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        running = true;
    }

    private void configureServer() {
        server.setExecutor(executor);
        for(SimpleAction action: actions) {
            server.createContext(action.getResourcePath(), action.toHandler());
        }
    }

    private void ensureRunning() {
        if (!running) {
            String msg = "Server not started, cannot perform operation.";
            throw new IllegalStateException(msg);
        }
    }

    /**
     * Creates a basic server using a default backlog and executor.
     */
    public static SimpleHttpServer createBasic(String hostname, int port,
                                               Set<SimpleAction> actions) {
        return new SimpleHttpServer(hostname, port, 0, null, actions);
    }

    /**
     * Creates a new {@link Builder} object.
     */
    public SimpleHttpServer.Builder newBuilder() {
        return new SimpleHttpServer.Builder();
    }

    /**
     * Converts the given object to a {@link Builder}.
     */
    public SimpleHttpServer.Builder toBuilder() {
        return newBuilder()
                .setHostname(hostname)
                .setPort(port)
                .setBacklog(backlog)
                .setExecutor(executor)
                .setActions(actions);
    }

    /**
     * Builder for creating a new {@link SimpleHttpServer}.
     */
    public static class Builder {

        private String hostname;
        private Integer port;
        private Integer backlog;
        private Executor executor;
        private Set<SimpleAction> actions;

        public Builder setHostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        /**
         * Sets the port for the server.
         */
        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        /**
         * Sets the backlog for the server. A 0-value will use the system
         * default.
         */
        public Builder setBacklog(int backlog) {
            this.backlog = backlog;
            return this;
        }

        /**
         * Sets the executor for the server. To handle more than one request
         * at the same time, a multi-threaded executor will need to be provided.
         * A null executor will cause the main thread to be used.
         */
        public Builder setExecutor(Executor executor) {
            this.executor = executor;
            return this;
        }

        /**
         * Sets the actions available on the server, overriding any existing
         * actions that have been set.
         */
        public Builder setActions(Set<SimpleAction> actions) {
            this.actions = actions;
            return this;
        }

        /**
         * Adds the given action to the server.
         */
        public Builder addAction(SimpleAction action) {
            convertNullToEmptyActions();
            actions.add(action);
            return this;
        }

        /**
         * Adds all the given actions to the server.
         */
        public Builder addAllActions(Set<SimpleAction> actions) {
            convertNullToEmptyActions();
            this.actions.addAll(actions);
            return this;
        }

        /**
         * Creates a new {@link SimpleHttpServer} from this builder.
         */
        public SimpleHttpServer build() {
            ensureNonNull(hostname, "address");
            ensureNonNull(port, "port");
            ensureNonNull(backlog, "backlog");
            convertNullToEmptyActions();

            return new SimpleHttpServer(hostname, port, backlog, executor,
                    actions);
        }

        private static void ensureNonNull(Object object, String fieldName) {
            if (object == null) {
                String msg = "The " + fieldName + " field must be non-null";
                throw new IllegalStateException(msg);
            }
        }

        private void convertNullToEmptyActions() {
            if (actions == null) {
                actions = new HashSet<>();
            }
        }
    }
}
