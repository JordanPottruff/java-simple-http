package simplehttp;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * A sender that dispatches responses to the HTTP client. The main benefit of
 * the dispatcher is that it can support chunked transfer encoding, which
 * allows responses to be sent in chunks. This can effectively allow data to
 * be streamed in real time to the client.
 *
 * The {@link #send} method can be used to do an ordinary, one time response
 * back to the client. For chunked transfer encoding, the
 * {@link #sendNextChunk} can be used to transfer chunks of data, and the
 * connection can be closed by calling {@link #endChunkEncoding()}.
 */
public class ResponseSender {

    private final HttpExchange exchange;

    private ResponseStatus responseStatus = ResponseStatus.READY;

    ResponseSender(HttpExchange exchange) {
        this.exchange = exchange;
    }

    /**
     * Sends an ordinary HTTP response back to the client.
     * @param response the response to send back to the client.
     * @throws IllegalStateException if a response has already been sent.
     */
    public void send(SimpleResponse response) {
        if (responseStatus != ResponseStatus.READY) {
            String msg = "Can only send one non-chunked response per request.";
            throw new IllegalStateException(msg);
        }
        byte[] body = response.getBodyRaw();
        try {
            exchange.getResponseHeaders().putAll(response.getHeaders().toMap());
            exchange.sendResponseHeaders(response.getStatusCode(), body.length);
            exchange.getResponseBody().write(body);
            exchange.getResponseBody().close();
            responseStatus = ResponseStatus.SENT;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Sends a chunk of data back to the client.
     * @param response the response to be sent. The header information will
     * only be sent if this is the first chunk of data.
     * @throws IllegalStateException if a completed response has already been
     * sent by calling {@link #send} or {@link #endChunkEncoding()}.
     */
    public void sendNextChunk(SimpleResponse response) {
        System.out.println("next chunk");
        if (responseStatus != ResponseStatus.READY &&
                responseStatus != ResponseStatus.CHUNKING) {
            String msg = "Can only send a chunked response first or " +
                    "after another chunked response.";
            throw new IllegalStateException(msg);
        }
        byte[] body = response.getBodyRaw();
        try {
            System.out.println(response.getBodyString());
            if (responseStatus != ResponseStatus.CHUNKING) {
                exchange.getResponseHeaders().putAll(response.getHeaders().toMap());
                exchange.sendResponseHeaders(response.getStatusCode(), 0);
            }
            exchange.getResponseBody().write(body);
            exchange.getResponseBody().flush();
            System.out.println("Chunk sent");
            responseStatus = ResponseStatus.CHUNKING;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Ends the chunk encoding stream, closing the connection to the client
     * and finishing the HTTP response.
     * @throws IllegalStateException if chunks of data were not previously
     * streamed to the client using {@link #sendNextChunk(SimpleResponse)}.
     */
    public void endChunkEncoding() {
        if (responseStatus != ResponseStatus.CHUNKING) {
            String msg = "Can only send a final chunked response after " +
                    "previous chunked responses.";
            throw new IllegalStateException(msg);
        }
        try {
            exchange.getResponseBody().close();
            responseStatus = ResponseStatus.SENT;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private enum ResponseStatus {
        READY,
        CHUNKING,
        SENT,
    }
}
