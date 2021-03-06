package org.elephant.client;

import org.elephant.timeserver.CurrentTimeRequest;
import org.elephant.timeserver.CurrentTimeResponse;
import org.elephant.timeserver.TimeServerGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class BenchmarkUpdater implements Callback<CurrentTimeResponse> {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final Semaphore semaphore = new Semaphore(50000);

    private final TimeServerGrpc.TimeServerStub stub;
    private final int maxRequests;
    private final boolean trace;

    BenchmarkUpdater(TimeServerGrpc.TimeServerStub stub, int maxRequests, boolean trace) {
        this.stub = stub;
        this.maxRequests = maxRequests;
        this.trace = trace;
    }

    private AtomicInteger responseCount = new AtomicInteger();
    private AtomicInteger errorCount = new AtomicInteger();
    private AtomicInteger successCount = new AtomicInteger();

    @Override
    public void done(CurrentTimeResponse currentTimeResponse, Throwable exception) {
        responseCount.incrementAndGet();
        if (exception == null && currentTimeResponse.getTime() > 0) {
            successCount.incrementAndGet();
        } else {
            errorCount.incrementAndGet();
        }
    }

    void runSync() {
        for (int count = 1; count <= maxRequests; count++) {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                logger.error("Error acquiring semaphore ", e);
            }
            CurrentTimeRequest request = CurrentTimeRequest.newBuilder().setCaller("NGO").build();
            stub.currentTime(request, new CustomStreamObserver<CurrentTimeResponse>(this, trace) {
                @Override
                public void onCompleted() {
                    semaphore.release();
                    super.onCompleted();
                }
            });
        }
        await();
    }

    void print() {
        logger.info("-------------------------------------------------------------------");
        logger.info("Response: Total {}, Success {}, error {}", responseCount.get(), successCount.get(), errorCount.get());
        logger.info("-------------------------------------------------------------------");
    }

    void await() {
        try {
            semaphore.acquire(50000);
        } catch (InterruptedException e) {
            logger.error("Error acquiring semaphore in await", e);
        }
    }
}
