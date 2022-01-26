package main;

import simplehttp.*;

@ForResource(path = "/action/foostream")
public class FooStreamAction extends SimpleAction {

    @Override
    public void handleGet(SimpleRequest request, ResponseSender sender) {
        int times = Integer.parseInt(request.getQueryParam("times"));
        int delay = Integer.parseInt(request.getQueryParam("delay"));
        SimpleHeaders headers = new SimpleHeaders.Builder()
                .add(HttpHeader.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .add(HttpHeader.CONTENT_TYPE, "text/plain")
                .build();
        for(int i=0; i<times; i++) {
            SimpleResponse response = new SimpleResponse.Builder()
                    .setBody("foo " + i + "\n")
                    .setStatusCode(HttpStatus.OK)
                    .setHeaders(headers)
                    .build();
            sender.sendNextChunk(response);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        sender.endChunkEncoding();
    }
}
