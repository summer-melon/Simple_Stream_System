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
public final class ManageJobGrpc {

  private ManageJobGrpc() {}

  public static final String SERVICE_NAME = "com.toutiao.melon.rpc.ManageJob";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<ManageJobRequest,
      ManageJobResponse> getManageJobMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ManageJob",
      requestType = ManageJobRequest.class,
      responseType = ManageJobResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<ManageJobRequest,
      ManageJobResponse> getManageJobMethod() {
    io.grpc.MethodDescriptor<ManageJobRequest, ManageJobResponse> getManageJobMethod;
    if ((getManageJobMethod = ManageJobGrpc.getManageJobMethod) == null) {
      synchronized (ManageJobGrpc.class) {
        if ((getManageJobMethod = ManageJobGrpc.getManageJobMethod) == null) {
          ManageJobGrpc.getManageJobMethod = getManageJobMethod =
              io.grpc.MethodDescriptor.<ManageJobRequest, ManageJobResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ManageJob"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ManageJobRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ManageJobResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ManageJobMethodDescriptorSupplier("ManageJob"))
              .build();
        }
      }
    }
    return getManageJobMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ManageJobStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ManageJobStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ManageJobStub>() {
        @Override
        public ManageJobStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ManageJobStub(channel, callOptions);
        }
      };
    return ManageJobStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ManageJobBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ManageJobBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ManageJobBlockingStub>() {
        @Override
        public ManageJobBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ManageJobBlockingStub(channel, callOptions);
        }
      };
    return ManageJobBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ManageJobFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ManageJobFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ManageJobFutureStub>() {
        @Override
        public ManageJobFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ManageJobFutureStub(channel, callOptions);
        }
      };
    return ManageJobFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class ManageJobImplBase implements io.grpc.BindableService {

    /**
     */
    public io.grpc.stub.StreamObserver<ManageJobRequest> manageJob(
        io.grpc.stub.StreamObserver<ManageJobResponse> responseObserver) {
      return asyncUnimplementedStreamingCall(getManageJobMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getManageJobMethod(),
            asyncClientStreamingCall(
              new MethodHandlers<
                ManageJobRequest,
                ManageJobResponse>(
                  this, METHODID_MANAGE_JOB)))
          .build();
    }
  }

  /**
   */
  public static final class ManageJobStub extends io.grpc.stub.AbstractAsyncStub<ManageJobStub> {
    private ManageJobStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ManageJobStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ManageJobStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<ManageJobRequest> manageJob(
        io.grpc.stub.StreamObserver<ManageJobResponse> responseObserver) {
      return asyncClientStreamingCall(
          getChannel().newCall(getManageJobMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   */
  public static final class ManageJobBlockingStub extends io.grpc.stub.AbstractBlockingStub<ManageJobBlockingStub> {
    private ManageJobBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ManageJobBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ManageJobBlockingStub(channel, callOptions);
    }
  }

  /**
   */
  public static final class ManageJobFutureStub extends io.grpc.stub.AbstractFutureStub<ManageJobFutureStub> {
    private ManageJobFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ManageJobFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ManageJobFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_MANAGE_JOB = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ManageJobImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ManageJobImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_MANAGE_JOB:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.manageJob(
              (io.grpc.stub.StreamObserver<ManageJobResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class ManageJobBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ManageJobBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return Master.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ManageJob");
    }
  }

  private static final class ManageJobFileDescriptorSupplier
      extends ManageJobBaseDescriptorSupplier {
    ManageJobFileDescriptorSupplier() {}
  }

  private static final class ManageJobMethodDescriptorSupplier
      extends ManageJobBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ManageJobMethodDescriptorSupplier(String methodName) {
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
      synchronized (ManageJobGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ManageJobFileDescriptorSupplier())
              .addMethod(getManageJobMethod())
              .build();
        }
      }
    }
    return result;
  }
}
