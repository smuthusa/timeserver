package org.elephant.client;

import com.google.common.base.Stopwatch;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CustomStreamObserver<T> implements StreamObserver<T> {
    private static final Logger logger = LoggerFactory.getLogger(CustomStreamObserver.class);
    private final List<T> response = new LinkedList<>();
    private final Stopwatch stopwatch;
    private Throwable exception;
    private Callback<T> callback;
    private boolean trace;

    CustomStreamObserver(Callback<T> callback, boolean trace) {
        this.callback = callback;
        this.trace = trace;
        this.stopwatch = Stopwatch.createStarted();
    }

    @Override
    public void onNext(T t) {
        response.add(t);
        if (trace) {
            logger.info("Time taken {}ms ", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    @Override
    public void onError(Throwable throwable) {
        exception = throwable;
    }

    @Override
    public void onCompleted() {
        callback.done(response.get(0), exception);
    }
}
