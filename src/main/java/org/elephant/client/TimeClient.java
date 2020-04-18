package org.elephant.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.elephant.LoggerUtil;
import org.elephant.timeserver.CurrentTimeRequest;
import org.elephant.timeserver.CurrentTimeResponse;
import org.elephant.timeserver.TimeServerGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Date;

public class TimeClient {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TimeServerGrpc.TimeServerBlockingStub stub;
    private boolean trace;

    private TimeClient(String address, int port, boolean trace) {
        this.trace = trace;
        ManagedChannel channel = ManagedChannelBuilder.forAddress(address, port).usePlaintext().build();
        stub = TimeServerGrpc.newBlockingStub(channel);
    }


    public static void main(String[] args) {
        LoggerUtil.logLevelToInfo();
        logger.info("Starting a client to benchmark");
        TimeClient client = new TimeClient("127.0.0.1", 9000, false);
        client.benchmark(1);
        client.benchmark(1000);
        client.benchmark(1000000);
    }

    private void benchmark(int maxRequests) {
        logger.info("Running {} requests ", maxRequests);
        long startTime = System.currentTimeMillis();
        for (int request = 1; request <= maxRequests; request++) {
            printCurrentTime(request);
        }
        logger.info("Total time taken for {} requests = {}ms ", maxRequests, (System.currentTimeMillis() - startTime));
    }


    private void printCurrentTime(int requestCount) {
        long startTime = System.currentTimeMillis();
        CurrentTimeRequest request = CurrentTimeRequest.newBuilder().setCaller("NGO").build();
        CurrentTimeResponse response = stub.currentTime(request);
        if (trace) {
            logger.info("Total time taken for request {} {} ", requestCount, (System.currentTimeMillis() - startTime));
            logger.debug("Current time {} ", new Date(response.getTime()));
        }
    }
}
