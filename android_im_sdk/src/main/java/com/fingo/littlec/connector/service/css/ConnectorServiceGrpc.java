package com.fingo.littlec.connector.service.css;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 * <pre>
 * 通知服务，用于服务端推送通知至客户端
 * </pre>
 */
@javax.annotation.Generated(
        value = "by gRPC proto compiler (version 1.10.0)",
        comments = "Source: css/grpc.proto")
public final class ConnectorServiceGrpc {

  private ConnectorServiceGrpc() {}

  public static final String SERVICE_NAME = "css.grpc.ConnectorService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getSendUnaryRequestMethod()} instead.
  public static final io.grpc.MethodDescriptor<com.fingo.littlec.proto.css.Connector.UnaryRequest,
          com.fingo.littlec.proto.css.Connector.UnaryResponse> METHOD_SEND_UNARY_REQUEST = getSendUnaryRequestMethodHelper();

  private static volatile io.grpc.MethodDescriptor<com.fingo.littlec.proto.css.Connector.UnaryRequest,
          com.fingo.littlec.proto.css.Connector.UnaryResponse> getSendUnaryRequestMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<com.fingo.littlec.proto.css.Connector.UnaryRequest,
          com.fingo.littlec.proto.css.Connector.UnaryResponse> getSendUnaryRequestMethod() {
    return getSendUnaryRequestMethodHelper();
  }

  private static io.grpc.MethodDescriptor<com.fingo.littlec.proto.css.Connector.UnaryRequest,
          com.fingo.littlec.proto.css.Connector.UnaryResponse> getSendUnaryRequestMethodHelper() {
    io.grpc.MethodDescriptor<com.fingo.littlec.proto.css.Connector.UnaryRequest, com.fingo.littlec.proto.css.Connector.UnaryResponse> getSendUnaryRequestMethod;
    if ((getSendUnaryRequestMethod = ConnectorServiceGrpc.getSendUnaryRequestMethod) == null) {
      synchronized (ConnectorServiceGrpc.class) {
        if ((getSendUnaryRequestMethod = ConnectorServiceGrpc.getSendUnaryRequestMethod) == null) {
          ConnectorServiceGrpc.getSendUnaryRequestMethod = getSendUnaryRequestMethod =
                  io.grpc.MethodDescriptor.<com.fingo.littlec.proto.css.Connector.UnaryRequest, com.fingo.littlec.proto.css.Connector.UnaryResponse>newBuilder()
                          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                          .setFullMethodName(generateFullMethodName(
                                  "css.grpc.ConnectorService", "SendUnaryRequest"))
                          .setSampledToLocalTracing(true)
                          .setRequestMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                                  com.fingo.littlec.proto.css.Connector.UnaryRequest.getDefaultInstance()))
                          .setResponseMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                                  com.fingo.littlec.proto.css.Connector.UnaryResponse.getDefaultInstance()))
                          .build();
        }
      }
    }
    return getSendUnaryRequestMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getSendSessionRequestMethod()} instead.
  public static final io.grpc.MethodDescriptor<com.fingo.littlec.proto.css.Connector.SessionRequest,
          com.fingo.littlec.proto.css.Connector.SessionNotify> METHOD_SEND_SESSION_REQUEST = getSendSessionRequestMethodHelper();

  private static volatile io.grpc.MethodDescriptor<com.fingo.littlec.proto.css.Connector.SessionRequest,
          com.fingo.littlec.proto.css.Connector.SessionNotify> getSendSessionRequestMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<com.fingo.littlec.proto.css.Connector.SessionRequest,
          com.fingo.littlec.proto.css.Connector.SessionNotify> getSendSessionRequestMethod() {
    return getSendSessionRequestMethodHelper();
  }

  private static io.grpc.MethodDescriptor<com.fingo.littlec.proto.css.Connector.SessionRequest,
          com.fingo.littlec.proto.css.Connector.SessionNotify> getSendSessionRequestMethodHelper() {
    io.grpc.MethodDescriptor<com.fingo.littlec.proto.css.Connector.SessionRequest, com.fingo.littlec.proto.css.Connector.SessionNotify> getSendSessionRequestMethod;
    if ((getSendSessionRequestMethod = ConnectorServiceGrpc.getSendSessionRequestMethod) == null) {
      synchronized (ConnectorServiceGrpc.class) {
        if ((getSendSessionRequestMethod = ConnectorServiceGrpc.getSendSessionRequestMethod) == null) {
          ConnectorServiceGrpc.getSendSessionRequestMethod = getSendSessionRequestMethod =
                  io.grpc.MethodDescriptor.<com.fingo.littlec.proto.css.Connector.SessionRequest, com.fingo.littlec.proto.css.Connector.SessionNotify>newBuilder()
                          .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
                          .setFullMethodName(generateFullMethodName(
                                  "css.grpc.ConnectorService", "SendSessionRequest"))
                          .setSampledToLocalTracing(true)
                          .setRequestMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                                  com.fingo.littlec.proto.css.Connector.SessionRequest.getDefaultInstance()))
                          .setResponseMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                                  com.fingo.littlec.proto.css.Connector.SessionNotify.getDefaultInstance()))
                          .build();
        }
      }
    }
    return getSendSessionRequestMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getHealthCheckMethod()} instead.
  public static final io.grpc.MethodDescriptor<com.fingo.littlec.proto.css.Connector.HealthCheckRequest,
          com.fingo.littlec.proto.css.Connector.HealthCheckResponse> METHOD_HEALTH_CHECK = getHealthCheckMethodHelper();

  private static volatile io.grpc.MethodDescriptor<com.fingo.littlec.proto.css.Connector.HealthCheckRequest,
          com.fingo.littlec.proto.css.Connector.HealthCheckResponse> getHealthCheckMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<com.fingo.littlec.proto.css.Connector.HealthCheckRequest,
          com.fingo.littlec.proto.css.Connector.HealthCheckResponse> getHealthCheckMethod() {
    return getHealthCheckMethodHelper();
  }

  private static io.grpc.MethodDescriptor<com.fingo.littlec.proto.css.Connector.HealthCheckRequest,
          com.fingo.littlec.proto.css.Connector.HealthCheckResponse> getHealthCheckMethodHelper() {
    io.grpc.MethodDescriptor<com.fingo.littlec.proto.css.Connector.HealthCheckRequest, com.fingo.littlec.proto.css.Connector.HealthCheckResponse> getHealthCheckMethod;
    if ((getHealthCheckMethod = ConnectorServiceGrpc.getHealthCheckMethod) == null) {
      synchronized (ConnectorServiceGrpc.class) {
        if ((getHealthCheckMethod = ConnectorServiceGrpc.getHealthCheckMethod) == null) {
          ConnectorServiceGrpc.getHealthCheckMethod = getHealthCheckMethod =
                  io.grpc.MethodDescriptor.<com.fingo.littlec.proto.css.Connector.HealthCheckRequest, com.fingo.littlec.proto.css.Connector.HealthCheckResponse>newBuilder()
                          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                          .setFullMethodName(generateFullMethodName(
                                  "css.grpc.ConnectorService", "HealthCheck"))
                          .setSampledToLocalTracing(true)
                          .setRequestMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                                  com.fingo.littlec.proto.css.Connector.HealthCheckRequest.getDefaultInstance()))
                          .setResponseMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                                  com.fingo.littlec.proto.css.Connector.HealthCheckResponse.getDefaultInstance()))
                          .build();
        }
      }
    }
    return getHealthCheckMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ConnectorServiceStub newStub(io.grpc.Channel channel) {
    return new ConnectorServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ConnectorServiceBlockingStub newBlockingStub(
          io.grpc.Channel channel) {
    return new ConnectorServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ConnectorServiceFutureStub newFutureStub(
          io.grpc.Channel channel) {
    return new ConnectorServiceFutureStub(channel);
  }

  /**
   * <pre>
   * 通知服务，用于服务端推送通知至客户端
   * </pre>
   */
  public static abstract class ConnectorServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * 一对一服务请求
     * </pre>
     */
    public void sendUnaryRequest(com.fingo.littlec.proto.css.Connector.UnaryRequest request,
                                 io.grpc.stub.StreamObserver<com.fingo.littlec.proto.css.Connector.UnaryResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSendUnaryRequestMethodHelper(), responseObserver);
    }

    /**
     * <pre>
     * Session相关操作
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.fingo.littlec.proto.css.Connector.SessionRequest> sendSessionRequest(
            io.grpc.stub.StreamObserver<com.fingo.littlec.proto.css.Connector.SessionNotify> responseObserver) {
      return asyncUnimplementedStreamingCall(getSendSessionRequestMethodHelper(), responseObserver);
    }

    /**
     * <pre>
     * health检测
     * </pre>
     */
    public void healthCheck(com.fingo.littlec.proto.css.Connector.HealthCheckRequest request,
                            io.grpc.stub.StreamObserver<com.fingo.littlec.proto.css.Connector.HealthCheckResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getHealthCheckMethodHelper(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
              .addMethod(
                      getSendUnaryRequestMethodHelper(),
                      asyncUnaryCall(
                              new MethodHandlers<
                                      com.fingo.littlec.proto.css.Connector.UnaryRequest,
                                      com.fingo.littlec.proto.css.Connector.UnaryResponse>(
                                      this, METHODID_SEND_UNARY_REQUEST)))
              .addMethod(
                      getSendSessionRequestMethodHelper(),
                      asyncBidiStreamingCall(
                              new MethodHandlers<
                                      com.fingo.littlec.proto.css.Connector.SessionRequest,
                                      com.fingo.littlec.proto.css.Connector.SessionNotify>(
                                      this, METHODID_SEND_SESSION_REQUEST)))
              .addMethod(
                      getHealthCheckMethodHelper(),
                      asyncUnaryCall(
                              new MethodHandlers<
                                      com.fingo.littlec.proto.css.Connector.HealthCheckRequest,
                                      com.fingo.littlec.proto.css.Connector.HealthCheckResponse>(
                                      this, METHODID_HEALTH_CHECK)))
              .build();
    }
  }

  /**
   * <pre>
   * 通知服务，用于服务端推送通知至客户端
   * </pre>
   */
  public static final class ConnectorServiceStub extends io.grpc.stub.AbstractStub<ConnectorServiceStub> {
    private ConnectorServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ConnectorServiceStub(io.grpc.Channel channel,
                                 io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ConnectorServiceStub build(io.grpc.Channel channel,
                                         io.grpc.CallOptions callOptions) {
      return new ConnectorServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * 一对一服务请求
     * </pre>
     */
    public void sendUnaryRequest(com.fingo.littlec.proto.css.Connector.UnaryRequest request,
                                 io.grpc.stub.StreamObserver<com.fingo.littlec.proto.css.Connector.UnaryResponse> responseObserver) {
      asyncUnaryCall(
              getChannel().newCall(getSendUnaryRequestMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Session相关操作
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.fingo.littlec.proto.css.Connector.SessionRequest> sendSessionRequest(
            io.grpc.stub.StreamObserver<com.fingo.littlec.proto.css.Connector.SessionNotify> responseObserver) {
      return asyncBidiStreamingCall(
              getChannel().newCall(getSendSessionRequestMethodHelper(), getCallOptions()), responseObserver);
    }

    /**
     * <pre>
     * health检测
     * </pre>
     */
    public void healthCheck(com.fingo.littlec.proto.css.Connector.HealthCheckRequest request,
                            io.grpc.stub.StreamObserver<com.fingo.littlec.proto.css.Connector.HealthCheckResponse> responseObserver) {
      asyncUnaryCall(
              getChannel().newCall(getHealthCheckMethodHelper(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * 通知服务，用于服务端推送通知至客户端
   * </pre>
   */
  public static final class ConnectorServiceBlockingStub extends io.grpc.stub.AbstractStub<ConnectorServiceBlockingStub> {
    private ConnectorServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ConnectorServiceBlockingStub(io.grpc.Channel channel,
                                         io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ConnectorServiceBlockingStub build(io.grpc.Channel channel,
                                                 io.grpc.CallOptions callOptions) {
      return new ConnectorServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * 一对一服务请求
     * </pre>
     */
    public com.fingo.littlec.proto.css.Connector.UnaryResponse sendUnaryRequest(com.fingo.littlec.proto.css.Connector.UnaryRequest request) {
      return blockingUnaryCall(
              getChannel(), getSendUnaryRequestMethodHelper(), getCallOptions(), request);
    }

    /**
     * <pre>
     * health检测
     * </pre>
     */
    public com.fingo.littlec.proto.css.Connector.HealthCheckResponse healthCheck(com.fingo.littlec.proto.css.Connector.HealthCheckRequest request) {
      return blockingUnaryCall(
              getChannel(), getHealthCheckMethodHelper(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * 通知服务，用于服务端推送通知至客户端
   * </pre>
   */
  public static final class ConnectorServiceFutureStub extends io.grpc.stub.AbstractStub<ConnectorServiceFutureStub> {
    private ConnectorServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ConnectorServiceFutureStub(io.grpc.Channel channel,
                                       io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ConnectorServiceFutureStub build(io.grpc.Channel channel,
                                               io.grpc.CallOptions callOptions) {
      return new ConnectorServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * 一对一服务请求
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.fingo.littlec.proto.css.Connector.UnaryResponse> sendUnaryRequest(
            com.fingo.littlec.proto.css.Connector.UnaryRequest request) {
      return futureUnaryCall(
              getChannel().newCall(getSendUnaryRequestMethodHelper(), getCallOptions()), request);
    }

    /**
     * <pre>
     * health检测
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.fingo.littlec.proto.css.Connector.HealthCheckResponse> healthCheck(
            com.fingo.littlec.proto.css.Connector.HealthCheckRequest request) {
      return futureUnaryCall(
              getChannel().newCall(getHealthCheckMethodHelper(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SEND_UNARY_REQUEST = 0;
  private static final int METHODID_HEALTH_CHECK = 1;
  private static final int METHODID_SEND_SESSION_REQUEST = 2;

  private static final class MethodHandlers<Req, Resp> implements
          io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ConnectorServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ConnectorServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND_UNARY_REQUEST:
          serviceImpl.sendUnaryRequest((com.fingo.littlec.proto.css.Connector.UnaryRequest) request,
                  (io.grpc.stub.StreamObserver<com.fingo.littlec.proto.css.Connector.UnaryResponse>) responseObserver);
          break;
        case METHODID_HEALTH_CHECK:
          serviceImpl.healthCheck((com.fingo.littlec.proto.css.Connector.HealthCheckRequest) request,
                  (io.grpc.stub.StreamObserver<com.fingo.littlec.proto.css.Connector.HealthCheckResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
            io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND_SESSION_REQUEST:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.sendSessionRequest(
                  (io.grpc.stub.StreamObserver<com.fingo.littlec.proto.css.Connector.SessionNotify>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ConnectorServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
                  .addMethod(getSendUnaryRequestMethodHelper())
                  .addMethod(getSendSessionRequestMethodHelper())
                  .addMethod(getHealthCheckMethodHelper())
                  .build();
        }
      }
    }
    return result;
  }
}
