package main;

import simplehttp.SimpleHttpServer;

import java.util.Set;
import java.util.concurrent.Executors;

public class Driver {


    public static void main(String[] args) {
        SimpleHttpServer server = new SimpleHttpServer.Builder()
                .setHostname("localhost")
            .setPort(8000)
            .setActions(Set.of(new FooAction(), new FooStreamAction()))
            .setBacklog(4)
            .setExecutor(Executors.newFixedThreadPool(3))
            .build();
        server.start();
    }
}
