package org.elephant.timeserver.TimeServer;


import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcTimeServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        final Server server = ServerBuilder.forPort(9000).addService(new TimeServerImpl()).build();
        server.start();
        server.awaitTermination();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.shutdown();
            }
        });
    }
}
