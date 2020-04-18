package org.elephant.timeserver.TimeServer;


import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.elephant.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

public class GrpcTimeServer {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) throws IOException, InterruptedException {
        LoggerUtil.logLevelToInfo();
        logger.info("Starting a GRPC service");
        final Server server = ServerBuilder.forPort(9000).addService(new TimeServerImpl()).build();
        server.start();
        logger.info("Listening for GRPC requests!!!");
        server.awaitTermination();
    }
}
