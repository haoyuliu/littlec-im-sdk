package com.littlec.sdk.connect.core;


import com.fingo.littlec.proto.css.CssErrorCode;
import com.littlec.sdk.connect.LCGrpcManager;
import com.littlec.sdk.lang.LCException;
import com.fingo.littlec.proto.css.Connector;

import io.grpc.stub.StreamObserver;

public class BaseClient {

    /**
     * 一对一同步请求
     *
     * @param request
     * @return
     */
    public static Connector.UnaryResponse sendUnaryRequest(Connector.UnaryRequest request, int timeout) throws LCException {
        Connector.UnaryResponse response = LCGrpcManager.getInstance().sendUnaryRequestWithDeadline(request, timeout);
        if (response.getRet() != CssErrorCode.ErrorCode.OK) {

            throw new LCException(response.getRet().getNumber(), response.getRet().name());
        }
        return response;
    }

    public static Connector.UnaryResponse sendUnaryRequest(Connector.UnaryRequest request) throws LCException {
        Connector.UnaryResponse response = LCGrpcManager.getInstance().sendUnaryRequest(request);
        if (response.getRet() != CssErrorCode.ErrorCode.OK) {
            throw new LCException(response.getRet().getNumber(), response.getRet().name());
        }
        return response;
    }

    /**
     * 异步请求
     *
     * @param request
     * @param responseObserver
     */
    public static void sendAsyncUnaryRequest(Connector.UnaryRequest request, StreamObserver<Connector.UnaryResponse> responseObserver) {
        LCGrpcManager.getInstance().asyncUnaryRequest(request, responseObserver);
    }
}