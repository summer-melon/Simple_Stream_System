package com.toutiao.melon.rpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.29.0)",
    comments = "Source: com/toutiao/melon/rpc/worker.proto")
public final class TransmitEventGrpc {

  private TransmitEventGrpc() {}

  public static final String SERVICE_NAME = "com.toutiao.melon.rpc.TransmitEvent";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<RpcEvent,
      com.google.protobuf.Empty> getTransmitEventMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "TransmitEvent",
      requestType = RpcEvent.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<RpcEvent,
      com.google.protobuf.Empty> getTransmitEventMethod() {
    io.grpc.MethodDescriptor<RpcEvent, com.google.protobuf.Empty> getTransmitEventMethod;
    if ((getTransmitEventMethod = TransmitEventGrpc.getTransmitEventMethod) == null) {
      synchronized (TransmitEventGrpc.class) {
        if ((getTransmitEventMethod = TransmitEventGrpc.getTransmitEventMethod) == null) {
          TransmitEventGrpc.getTransmitEventMethod = getTransmitEventMethod =
              io.grpc.MethodDescriptor.<RpcEvent, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "TransmitEvent"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  RpcEvent.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new TransmitEventMethodDescriptorSupplier("TransmitEvent"))
              .build();
        }
      }
    }
    return getTransmitEventMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static TransmitEventStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TransmitEventStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TransmitEventStub>() {
        @Override
        public TransmitEventStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TransmitEventStub(channel, callOptions);
        }
      };
    return TransmitEventStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TransmitEventBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TransmitEventBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TransmitEventBlockingStub>() {
        @Override
        public TransmitEventBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TransmitEventBlockingStub(channel, callOptions);
        }
      };
    return TransmitEventBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static TransmitEventFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TransmitEventFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TransmitEventFutureStub>() {
        @Override
        public TransmitEventFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TransmitEventFutureStub(channel, callOptions);
        }
      };
    return TransmitEventFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class TransmitEventImplBase implements io.grpc.BindableService {

    /**
     */
    public void transmitEvent(RpcEvent request,
                              io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getTransmitEventMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getTransmitEventMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                RpcEvent,
                com.google.protobuf.Empty>(
                  this, METHODID_TRANSMIT_EVENT)))
          .build();
    }
  }

  /**
   */
  public static final class TransmitEventStub extends io.grpc.stub.AbstractAsyncStub<TransmitEventStub> {
    private TransmitEventStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected TransmitEventStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TransmitEventStub(channel, callOptions);
    }

    /**
     */
    public void transmitEvent(RpcEvent request,
                              io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTransmitEventMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class TransmitEventBlockingStub extends io.grpc.stub.AbstractBlockingStub<TransmitEventBlockingStub> {
    private TransmitEventBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected TransmitEventBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TransmitEventBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.google.protobuf.Empty transmitEvent(RpcEvent request) {
      return blockingUnaryCall(
          getChannel(), getTransmitEventMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class TransmitEventFutureStub extends io.grpc.stub.AbstractFutureStub<TransmitEventFutureStub> {
    private TransmitEventFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected TransmitEventFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TransmitEventFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> transmitEvent(
        RpcEvent request) {
      return futureUnaryCall(
          getChannel().newCall(getTransmitEventMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_TRANSMIT_EVENT = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final TransmitEventImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(TransmitEventImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_TRANSMIT_EVENT:
          serviceImpl.transmitEvent((RpcEvent) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class TransmitEventBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    TransmitEventBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return Worker.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("TransmitEvent");
    }
  }

  private static final class TransmitEventFileDescriptorSupplier
      extends TransmitEventBaseDescriptorSupplier {
    TransmitEventFileDescriptorSupplier() {}
  }

  private static final class TransmitEventMethodDescriptorSupplier
      extends TransmitEventBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    TransmitEventMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (TransmitEventGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new TransmitEventFileDescriptorSupplier())
              .addMethod(getTransmitEventMethod())
              .build();
        }
      }
    }
    return result;
  }
}
