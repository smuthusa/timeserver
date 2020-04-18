package org.elephant.timeserver.TimeServer;

import io.grpc.stub.StreamObserver;
import org.elephant.timeserver.CurrentTimeRequest;
import org.elephant.timeserver.CurrentTimeResponse;
import org.elephant.timeserver.TimeServerGrpc;

public class TimeServerImpl extends TimeServerGrpc.TimeServerImplBase {

    @Override
    public void currentTime(CurrentTimeRequest request, StreamObserver<CurrentTimeResponse> responseObserver) {
        CurrentTimeResponse response = CurrentTimeResponse.newBuilder().setTime(System.currentTimeMillis()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
