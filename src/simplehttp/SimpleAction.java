package simplehttp;

import com.sun.net.httpserver.HttpHandler;

/**
 * An action provides the operations that can be performed on a resource. The
 * resource for the action is defined using the {@link ForResource}
 * annotation, and various methods can be overridden to provide the logic for
 * each request method (GET, POST, etc.). For example, the {@link #handleGet}
 * method corresponds to GET requests on the given resource. A handle method
 * that is not overridden will return a 404 Not Found error code.
 *
 * Each handle method has two parameters, a {@link SimpleRequest} that
 * contains the details of the HTTP request, and a {@link ResponseSender}
 * that is used to return a response, or a series of responses, back to the
 * HTTP client. The expectation is that a {@link SimpleResponse} object will
 * be created in each method and dispatched using the {@link ResponseSender}
 * as necessary.
 */
public abstract class SimpleAction {

    /**
     * Handles GET requests against the action's resource path.
     * @param request the content of the HTTP request.
     * @param responseSender a sender instance, which can be used to send
     * responses back to the HTTP client.
     */
    public void handleGet(SimpleRequest request, ResponseSender responseSender) {
        sendNotFoundError(responseSender);
    }

    /**
     * Handles POST requests against the action's resource path.
     * @param request the content of the HTTP request.
     * @param responseSender a sender instance, which can be used to send
     * responses back to the HTTP client.
     */
    public void handlePost(SimpleRequest request, ResponseSender responseSender) {
        sendNotFoundError(responseSender);
    }

    /**
     * Handles PATCH requests against the action's resource path.
     * @param request the content of the HTTP request.
     * @param responseSender a sender instance, which can be used to send
     * responses back to the HTTP client.
     */
    public void handlePatch(SimpleRequest request, ResponseSender responseSender) {
        sendNotFoundError(responseSender);
    }

    /**
     * Handles PUT requests against the action's resource path.
     * @param request the content of the HTTP request.
     * @param responseSender a sender instance, which can be used to send
     * responses back to the HTTP client.
     */
    public void handlePut(SimpleRequest request, ResponseSender responseSender) {
        sendNotFoundError(responseSender);
    }

    /**
     * Handles DELETE requests against the action's resource path.
     * @param request the content of the HTTP request.
     * @param responseSender a sender instance, which can be used to send
     * responses back to the HTTP client.
     */
    public void handleDelete(SimpleRequest request, ResponseSender responseSender) {
        sendNotFoundError(responseSender);
    }

    // Returns a 404 Not Found error back to the HTTP client.
    private void sendNotFoundError(ResponseSender responseSender) {
        SimpleResponse missingHandlerResponse = new SimpleResponse.Builder()
                .setStatusCode(HttpStatus.NOT_FOUND).build();
        responseSender.send(missingHandlerResponse);
    }

    // Converts the action to a native HttpHandler that can be used by
    // HttpServer.
    HttpHandler toHandler() {
        return exchange -> {
            // Must catch all exceptions to ensure they are logged to console.
            try {
                SimpleRequest request = SimpleRequest.fromExchange(exchange);
                ResponseSender responseSender = new ResponseSender(exchange);
                switch (exchange.getRequestMethod()) {
                    case "GET" -> handleGet(request, responseSender);
                    case "POST" -> handlePost(request, responseSender);
                    case "PATCH" -> handlePatch(request, responseSender);
                    case "PUT" -> handlePut(request, responseSender);
                    case "DELETE" -> handleDelete(request, responseSender);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    // Grabs the resource path from the annotation.
    String getResourcePath() {
        return this.getClass().getDeclaredAnnotation(ForResource.class).path();
    }
}
