package main;

import simplehttp.*;

@ForResource(path = "/action/foo")
public class FooAction extends SimpleAction {

    @Override
    public void handleGet(SimpleRequest request, ResponseSender sender) {
        String name = request.getQueryParam("name");
        String age = request.getQueryParam("age");
        String gender = request.getQueryParam("gender");

        String responseBody = String.format("We have a %s foo named %s who " +
                "is %s years old", gender, name, age);
        SimpleResponse response = new SimpleResponse.Builder()
                .setBody(responseBody)
                .setStatusCode(HttpStatus.OK)
                .build();

        sender.send(response);
    }

}
