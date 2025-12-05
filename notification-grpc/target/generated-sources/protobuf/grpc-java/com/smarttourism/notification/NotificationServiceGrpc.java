package com.smarttourism.notification;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Service de notification en temps réel
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.61.0)",
    comments = "Source: notification.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class NotificationServiceGrpc {

  private NotificationServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "notification.NotificationService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.smarttourism.notification.AlertRequest,
      com.smarttourism.notification.AlertResponse> getSendAlertMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SendAlert",
      requestType = com.smarttourism.notification.AlertRequest.class,
      responseType = com.smarttourism.notification.AlertResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.smarttourism.notification.AlertRequest,
      com.smarttourism.notification.AlertResponse> getSendAlertMethod() {
    io.grpc.MethodDescriptor<com.smarttourism.notification.AlertRequest, com.smarttourism.notification.AlertResponse> getSendAlertMethod;
    if ((getSendAlertMethod = NotificationServiceGrpc.getSendAlertMethod) == null) {
      synchronized (NotificationServiceGrpc.class) {
        if ((getSendAlertMethod = NotificationServiceGrpc.getSendAlertMethod) == null) {
          NotificationServiceGrpc.getSendAlertMethod = getSendAlertMethod =
              io.grpc.MethodDescriptor.<com.smarttourism.notification.AlertRequest, com.smarttourism.notification.AlertResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SendAlert"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smarttourism.notification.AlertRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smarttourism.notification.AlertResponse.getDefaultInstance()))
              .setSchemaDescriptor(new NotificationServiceMethodDescriptorSupplier("SendAlert"))
              .build();
        }
      }
    }
    return getSendAlertMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.smarttourism.notification.StreamRequest,
      com.smarttourism.notification.AlertResponse> getStreamAlertsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StreamAlerts",
      requestType = com.smarttourism.notification.StreamRequest.class,
      responseType = com.smarttourism.notification.AlertResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<com.smarttourism.notification.StreamRequest,
      com.smarttourism.notification.AlertResponse> getStreamAlertsMethod() {
    io.grpc.MethodDescriptor<com.smarttourism.notification.StreamRequest, com.smarttourism.notification.AlertResponse> getStreamAlertsMethod;
    if ((getStreamAlertsMethod = NotificationServiceGrpc.getStreamAlertsMethod) == null) {
      synchronized (NotificationServiceGrpc.class) {
        if ((getStreamAlertsMethod = NotificationServiceGrpc.getStreamAlertsMethod) == null) {
          NotificationServiceGrpc.getStreamAlertsMethod = getStreamAlertsMethod =
              io.grpc.MethodDescriptor.<com.smarttourism.notification.StreamRequest, com.smarttourism.notification.AlertResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "StreamAlerts"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smarttourism.notification.StreamRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smarttourism.notification.AlertResponse.getDefaultInstance()))
              .setSchemaDescriptor(new NotificationServiceMethodDescriptorSupplier("StreamAlerts"))
              .build();
        }
      }
    }
    return getStreamAlertsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.smarttourism.notification.CheckRequest,
      com.smarttourism.notification.AlertListResponse> getCheckActiveAlertsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CheckActiveAlerts",
      requestType = com.smarttourism.notification.CheckRequest.class,
      responseType = com.smarttourism.notification.AlertListResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.smarttourism.notification.CheckRequest,
      com.smarttourism.notification.AlertListResponse> getCheckActiveAlertsMethod() {
    io.grpc.MethodDescriptor<com.smarttourism.notification.CheckRequest, com.smarttourism.notification.AlertListResponse> getCheckActiveAlertsMethod;
    if ((getCheckActiveAlertsMethod = NotificationServiceGrpc.getCheckActiveAlertsMethod) == null) {
      synchronized (NotificationServiceGrpc.class) {
        if ((getCheckActiveAlertsMethod = NotificationServiceGrpc.getCheckActiveAlertsMethod) == null) {
          NotificationServiceGrpc.getCheckActiveAlertsMethod = getCheckActiveAlertsMethod =
              io.grpc.MethodDescriptor.<com.smarttourism.notification.CheckRequest, com.smarttourism.notification.AlertListResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CheckActiveAlerts"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smarttourism.notification.CheckRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smarttourism.notification.AlertListResponse.getDefaultInstance()))
              .setSchemaDescriptor(new NotificationServiceMethodDescriptorSupplier("CheckActiveAlerts"))
              .build();
        }
      }
    }
    return getCheckActiveAlertsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static NotificationServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NotificationServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NotificationServiceStub>() {
        @java.lang.Override
        public NotificationServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NotificationServiceStub(channel, callOptions);
        }
      };
    return NotificationServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static NotificationServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NotificationServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NotificationServiceBlockingStub>() {
        @java.lang.Override
        public NotificationServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NotificationServiceBlockingStub(channel, callOptions);
        }
      };
    return NotificationServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static NotificationServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NotificationServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NotificationServiceFutureStub>() {
        @java.lang.Override
        public NotificationServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NotificationServiceFutureStub(channel, callOptions);
        }
      };
    return NotificationServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Service de notification en temps réel
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Envoi d'une alerte simple
     * </pre>
     */
    default void sendAlert(com.smarttourism.notification.AlertRequest request,
        io.grpc.stub.StreamObserver<com.smarttourism.notification.AlertResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSendAlertMethod(), responseObserver);
    }

    /**
     * <pre>
     * Stream d'alertes en direct
     * </pre>
     */
    default void streamAlerts(com.smarttourism.notification.StreamRequest request,
        io.grpc.stub.StreamObserver<com.smarttourism.notification.AlertResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getStreamAlertsMethod(), responseObserver);
    }

    /**
     * <pre>
     * Vérification d'alertes actives
     * </pre>
     */
    default void checkActiveAlerts(com.smarttourism.notification.CheckRequest request,
        io.grpc.stub.StreamObserver<com.smarttourism.notification.AlertListResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCheckActiveAlertsMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service NotificationService.
   * <pre>
   * Service de notification en temps réel
   * </pre>
   */
  public static abstract class NotificationServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return NotificationServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service NotificationService.
   * <pre>
   * Service de notification en temps réel
   * </pre>
   */
  public static final class NotificationServiceStub
      extends io.grpc.stub.AbstractAsyncStub<NotificationServiceStub> {
    private NotificationServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NotificationServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NotificationServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Envoi d'une alerte simple
     * </pre>
     */
    public void sendAlert(com.smarttourism.notification.AlertRequest request,
        io.grpc.stub.StreamObserver<com.smarttourism.notification.AlertResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSendAlertMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Stream d'alertes en direct
     * </pre>
     */
    public void streamAlerts(com.smarttourism.notification.StreamRequest request,
        io.grpc.stub.StreamObserver<com.smarttourism.notification.AlertResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getStreamAlertsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Vérification d'alertes actives
     * </pre>
     */
    public void checkActiveAlerts(com.smarttourism.notification.CheckRequest request,
        io.grpc.stub.StreamObserver<com.smarttourism.notification.AlertListResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCheckActiveAlertsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service NotificationService.
   * <pre>
   * Service de notification en temps réel
   * </pre>
   */
  public static final class NotificationServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<NotificationServiceBlockingStub> {
    private NotificationServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NotificationServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NotificationServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Envoi d'une alerte simple
     * </pre>
     */
    public com.smarttourism.notification.AlertResponse sendAlert(com.smarttourism.notification.AlertRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSendAlertMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Stream d'alertes en direct
     * </pre>
     */
    public java.util.Iterator<com.smarttourism.notification.AlertResponse> streamAlerts(
        com.smarttourism.notification.StreamRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getStreamAlertsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Vérification d'alertes actives
     * </pre>
     */
    public com.smarttourism.notification.AlertListResponse checkActiveAlerts(com.smarttourism.notification.CheckRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCheckActiveAlertsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service NotificationService.
   * <pre>
   * Service de notification en temps réel
   * </pre>
   */
  public static final class NotificationServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<NotificationServiceFutureStub> {
    private NotificationServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NotificationServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NotificationServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Envoi d'une alerte simple
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.smarttourism.notification.AlertResponse> sendAlert(
        com.smarttourism.notification.AlertRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSendAlertMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Vérification d'alertes actives
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.smarttourism.notification.AlertListResponse> checkActiveAlerts(
        com.smarttourism.notification.CheckRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCheckActiveAlertsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SEND_ALERT = 0;
  private static final int METHODID_STREAM_ALERTS = 1;
  private static final int METHODID_CHECK_ACTIVE_ALERTS = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND_ALERT:
          serviceImpl.sendAlert((com.smarttourism.notification.AlertRequest) request,
              (io.grpc.stub.StreamObserver<com.smarttourism.notification.AlertResponse>) responseObserver);
          break;
        case METHODID_STREAM_ALERTS:
          serviceImpl.streamAlerts((com.smarttourism.notification.StreamRequest) request,
              (io.grpc.stub.StreamObserver<com.smarttourism.notification.AlertResponse>) responseObserver);
          break;
        case METHODID_CHECK_ACTIVE_ALERTS:
          serviceImpl.checkActiveAlerts((com.smarttourism.notification.CheckRequest) request,
              (io.grpc.stub.StreamObserver<com.smarttourism.notification.AlertListResponse>) responseObserver);
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
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getSendAlertMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.smarttourism.notification.AlertRequest,
              com.smarttourism.notification.AlertResponse>(
                service, METHODID_SEND_ALERT)))
        .addMethod(
          getStreamAlertsMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              com.smarttourism.notification.StreamRequest,
              com.smarttourism.notification.AlertResponse>(
                service, METHODID_STREAM_ALERTS)))
        .addMethod(
          getCheckActiveAlertsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.smarttourism.notification.CheckRequest,
              com.smarttourism.notification.AlertListResponse>(
                service, METHODID_CHECK_ACTIVE_ALERTS)))
        .build();
  }

  private static abstract class NotificationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    NotificationServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.smarttourism.notification.NotificationProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("NotificationService");
    }
  }

  private static final class NotificationServiceFileDescriptorSupplier
      extends NotificationServiceBaseDescriptorSupplier {
    NotificationServiceFileDescriptorSupplier() {}
  }

  private static final class NotificationServiceMethodDescriptorSupplier
      extends NotificationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    NotificationServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (NotificationServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new NotificationServiceFileDescriptorSupplier())
              .addMethod(getSendAlertMethod())
              .addMethod(getStreamAlertsMethod())
              .addMethod(getCheckActiveAlertsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
