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
    comments = "Source: com/toutiao/melon/rpc/master.proto")
public final class ProvideJarGrpc {

  private ProvideJarGrpc() {}

  public static final String SERVICE_NAME = "com.toutiao.melon.rpc.ProvideJar";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<ProvideJarRequest,
      ProvideJarResponse> getProvideJarMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ProvideJar",
      requestType = ProvideJarRequest.class,
      responseType = ProvideJarResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<ProvideJarRequest,
      ProvideJarResponse> getProvideJarMethod() {
    io.grpc.MethodDescriptor<ProvideJarRequest, ProvideJarResponse> getProvideJarMethod;
    if ((getProvideJarMethod = ProvideJarGrpc.getProvideJarMethod) == null) {
      synchronized (ProvideJarGrpc.class) {
        if ((getProvideJarMethod = ProvideJarGrpc.getProvideJarMethod) == null) {
          ProvideJarGrpc.getProvideJarMethod = getProvideJarMethod =
              io.grpc.MethodDescriptor.<ProvideJarRequest, ProvideJarResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ProvideJar"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ProvideJarRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ProvideJarResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ProvideJarMethodDescriptorSupplier("ProvideJar"))
              .build();
        }
      }
    }
    return getProvideJarMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ProvideJarStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProvideJarStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProvideJarStub>() {
        @Override
        public ProvideJarStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProvideJarStub(channel, callOptions);
        }
      };
    return ProvideJarStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ProvideJarBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProvideJarBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProvideJarBlockingStub>() {
        @Override
        public ProvideJarBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProvideJarBlockingStub(channel, callOptions);
        }
      };
    return ProvideJarBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ProvideJarFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProvideJarFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProvideJarFutureStub>() {
        @Override
        public ProvideJarFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProvideJarFutureStub(channel, callOptions);
        }
      };
    return ProvideJarFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class ProvideJarImplBase implements io.grpc.BindableService {

    /**
     */
    public void provideJar(ProvideJarRequest request,
                           io.grpc.stub.StreamObserver<ProvideJarResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getProvideJarMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getProvideJarMethod(),
            asyncServerStreamingCall(
              new MethodHandlers<
                ProvideJarRequest,
                ProvideJarResponse>(
                  this, METHODID_PROVIDE_JAR)))
          .build();
    }
  }

  /**
   */
  public static final class ProvideJarStub extends io.grpc.stub.AbstractAsyncStub<ProvideJarStub> {
    private ProvideJarStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ProvideJarStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProvideJarStub(channel, callOptions);
    }

    /**
     */
    public void provideJar(ProvideJarRequest request,
                           io.grpc.stub.StreamObserver<ProvideJarResponse> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getProvideJarMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ProvideJarBlockingStub extends io.grpc.stub.AbstractBlockingStub<ProvideJarBlockingStub> {
    private ProvideJarBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ProvideJarBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProvideJarBlockingStub(channel, callOptions);
    }

    /**
     */
    public java.util.Iterator<ProvideJarResponse> provideJar(
        ProvideJarRequest request) {
      return blockingServerStreamingCall(
          getChannel(), getProvideJarMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ProvideJarFutureStub extends io.grpc.stub.AbstractFutureStub<ProvideJarFutureStub> {
    private ProvideJarFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ProvideJarFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProvideJarFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_PROVIDE_JAR = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ProvideJarImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ProvideJarImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PROVIDE_JAR:
          serviceImpl.provideJar((ProvideJarRequest) request,
              (io.grpc.stub.StreamObserver<ProvideJarResponse>) responseObserver);
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

  private static abstract class ProvideJarBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ProvideJarBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return Master.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ProvideJar");
    }
  }

  private static final class ProvideJarFileDescriptorSupplier
      extends ProvideJarBaseDescriptorSupplier {
    ProvideJarFileDescriptorSupplier() {}
  }

  private static final class ProvideJarMethodDescriptorSupplier
      extends ProvideJarBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ProvideJarMethodDescriptorSupplier(String methodName) {
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
      synchronized (ProvideJarGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ProvideJarFileDescriptorSupplier())
              .addMethod(getProvideJarMethod())
              .build();
        }
      }
    }
    return result;
  }
}
