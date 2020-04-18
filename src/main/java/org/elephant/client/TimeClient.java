package org.elephant.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.elephant.LoggerUtil;
import org.elephant.timeserver.TimeServerGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;


public class TimeClient {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TimeServerGrpc.TimeServerStub stub;

    private TimeClient(String address, int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(address, port).usePlaintext().build();
        stub = TimeServerGrpc.newStub(channel);
    }


    public static void main(String[] args) throws InterruptedException {
        LoggerUtil.logLevelToInfo();
        logger.info("GRPC Throughput testing");
        final TimeClient client = new TimeClient("127.0.0.1", 9000);
        client.benchmark(1, 1);
        IntStream.range(1, 10).forEach(batch -> {
            try {
                client.benchmark(batch, batch * 100000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void benchmark(int batch, int maxRequests) throws InterruptedException {
        logger.info("[Batch-{}] Running {} requests ", batch, maxRequests);
        long startTime = System.currentTimeMillis();
        BenchmarkUpdater benchmark = new BenchmarkUpdater(stub, maxRequests, false);
        benchmark.start();
        benchmark.await();
        //benchmark.print();
        logger.info("[Batch-{}] Total time taken for {} requests = {}ms ", batch, maxRequests, (System.currentTimeMillis() - startTime));
    }
}
