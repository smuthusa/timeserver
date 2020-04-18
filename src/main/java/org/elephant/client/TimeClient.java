package org.elephant.client;

import com.google.common.base.Stopwatch;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.elephant.LoggerUtil;
import org.elephant.timeserver.TimeServerGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;


public class TimeClient {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int PARALLEL_THREADS = 10;
    private String address;
    private int port;

    private TimeClient(String address, int port) {
        this.address = address;
        this.port = port;
    }


    public static void main(String[] args) {
        LoggerUtil.logLevelToInfo();
        final TimeClient client = new TimeClient("127.0.0.1", 9000);
        client.benchmark(1, 1);
        ExecutorService executor = Executors.newFixedThreadPool(PARALLEL_THREADS);
        IntStream.rangeClosed(1, 10).forEach(batch -> executor.submit(() -> client.benchmark(batch, 1_000_000)));
    }

    private void benchmark(int batch, int maxRequests) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(address, port).usePlaintext().build();
        TimeServerGrpc.TimeServerStub stub = TimeServerGrpc.newStub(channel);
        Stopwatch stopwatch = Stopwatch.createStarted();
        BenchmarkUpdater benchmark = new BenchmarkUpdater(stub, maxRequests, false);
        benchmark.runSync();
        logger.info("[Batch-{}] Time taken for {} requests: {}ms ", batch, maxRequests, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        channel.shutdown();
    }
}
