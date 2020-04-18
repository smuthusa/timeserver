package org.elephant.client;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class CustomStreamObserver<T> implements StreamObserver<T> {
    private static final Logger logger = LoggerFactory.getLogger(CustomStreamObserver.class);
    private final List<T> response = new LinkedList<>();
    private final long startTime;
    private Throwable exception;
    private Callback<T> callback;
    private boolean trace;

    CustomStreamObserver(Callback<T> callback, boolean trace) {
        this.callback = callback;
        this.trace = trace;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void onNext(T t) {
        response.add(t);
        if (trace) {
            logger.info("Time taken {} ", (System.currentTimeMillis() - startTime));
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
