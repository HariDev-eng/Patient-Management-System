package doctor;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.69.0)",
    comments = "Source: doctor.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class DoctorServiceGrpc {

  private DoctorServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "doctor.DoctorService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<doctor.DoctorExistsRequest,
      doctor.DoctorExistsResponse> getDoctorExistsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DoctorExists",
      requestType = doctor.DoctorExistsRequest.class,
      responseType = doctor.DoctorExistsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<doctor.DoctorExistsRequest,
      doctor.DoctorExistsResponse> getDoctorExistsMethod() {
    io.grpc.MethodDescriptor<doctor.DoctorExistsRequest, doctor.DoctorExistsResponse> getDoctorExistsMethod;
    if ((getDoctorExistsMethod = DoctorServiceGrpc.getDoctorExistsMethod) == null) {
      synchronized (DoctorServiceGrpc.class) {
        if ((getDoctorExistsMethod = DoctorServiceGrpc.getDoctorExistsMethod) == null) {
          DoctorServiceGrpc.getDoctorExistsMethod = getDoctorExistsMethod =
              io.grpc.MethodDescriptor.<doctor.DoctorExistsRequest, doctor.DoctorExistsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DoctorExists"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  doctor.DoctorExistsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  doctor.DoctorExistsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DoctorServiceMethodDescriptorSupplier("DoctorExists"))
              .build();
        }
      }
    }
    return getDoctorExistsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<doctor.DoctorAvailabilityRequest,
      doctor.DoctorAvailabilityResponse> getCheckAvailabilityMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CheckAvailability",
      requestType = doctor.DoctorAvailabilityRequest.class,
      responseType = doctor.DoctorAvailabilityResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<doctor.DoctorAvailabilityRequest,
      doctor.DoctorAvailabilityResponse> getCheckAvailabilityMethod() {
    io.grpc.MethodDescriptor<doctor.DoctorAvailabilityRequest, doctor.DoctorAvailabilityResponse> getCheckAvailabilityMethod;
    if ((getCheckAvailabilityMethod = DoctorServiceGrpc.getCheckAvailabilityMethod) == null) {
      synchronized (DoctorServiceGrpc.class) {
        if ((getCheckAvailabilityMethod = DoctorServiceGrpc.getCheckAvailabilityMethod) == null) {
          DoctorServiceGrpc.getCheckAvailabilityMethod = getCheckAvailabilityMethod =
              io.grpc.MethodDescriptor.<doctor.DoctorAvailabilityRequest, doctor.DoctorAvailabilityResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CheckAvailability"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  doctor.DoctorAvailabilityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  doctor.DoctorAvailabilityResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DoctorServiceMethodDescriptorSupplier("CheckAvailability"))
              .build();
        }
      }
    }
    return getCheckAvailabilityMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DoctorServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DoctorServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DoctorServiceStub>() {
        @java.lang.Override
        public DoctorServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DoctorServiceStub(channel, callOptions);
        }
      };
    return DoctorServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DoctorServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DoctorServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DoctorServiceBlockingStub>() {
        @java.lang.Override
        public DoctorServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DoctorServiceBlockingStub(channel, callOptions);
        }
      };
    return DoctorServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DoctorServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DoctorServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DoctorServiceFutureStub>() {
        @java.lang.Override
        public DoctorServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DoctorServiceFutureStub(channel, callOptions);
        }
      };
    return DoctorServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void doctorExists(doctor.DoctorExistsRequest request,
        io.grpc.stub.StreamObserver<doctor.DoctorExistsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDoctorExistsMethod(), responseObserver);
    }

    /**
     */
    default void checkAvailability(doctor.DoctorAvailabilityRequest request,
        io.grpc.stub.StreamObserver<doctor.DoctorAvailabilityResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCheckAvailabilityMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service DoctorService.
   */
  public static abstract class DoctorServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return DoctorServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service DoctorService.
   */
  public static final class DoctorServiceStub
      extends io.grpc.stub.AbstractAsyncStub<DoctorServiceStub> {
    private DoctorServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DoctorServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DoctorServiceStub(channel, callOptions);
    }

    /**
     */
    public void doctorExists(doctor.DoctorExistsRequest request,
        io.grpc.stub.StreamObserver<doctor.DoctorExistsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDoctorExistsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void checkAvailability(doctor.DoctorAvailabilityRequest request,
        io.grpc.stub.StreamObserver<doctor.DoctorAvailabilityResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCheckAvailabilityMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service DoctorService.
   */
  public static final class DoctorServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<DoctorServiceBlockingStub> {
    private DoctorServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DoctorServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DoctorServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public doctor.DoctorExistsResponse doctorExists(doctor.DoctorExistsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDoctorExistsMethod(), getCallOptions(), request);
    }

    /**
     */
    public doctor.DoctorAvailabilityResponse checkAvailability(doctor.DoctorAvailabilityRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCheckAvailabilityMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service DoctorService.
   */
  public static final class DoctorServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<DoctorServiceFutureStub> {
    private DoctorServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DoctorServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DoctorServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<doctor.DoctorExistsResponse> doctorExists(
        doctor.DoctorExistsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDoctorExistsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<doctor.DoctorAvailabilityResponse> checkAvailability(
        doctor.DoctorAvailabilityRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCheckAvailabilityMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_DOCTOR_EXISTS = 0;
  private static final int METHODID_CHECK_AVAILABILITY = 1;

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
        case METHODID_DOCTOR_EXISTS:
          serviceImpl.doctorExists((doctor.DoctorExistsRequest) request,
              (io.grpc.stub.StreamObserver<doctor.DoctorExistsResponse>) responseObserver);
          break;
        case METHODID_CHECK_AVAILABILITY:
          serviceImpl.checkAvailability((doctor.DoctorAvailabilityRequest) request,
              (io.grpc.stub.StreamObserver<doctor.DoctorAvailabilityResponse>) responseObserver);
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
          getDoctorExistsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              doctor.DoctorExistsRequest,
              doctor.DoctorExistsResponse>(
                service, METHODID_DOCTOR_EXISTS)))
        .addMethod(
          getCheckAvailabilityMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              doctor.DoctorAvailabilityRequest,
              doctor.DoctorAvailabilityResponse>(
                service, METHODID_CHECK_AVAILABILITY)))
        .build();
  }

  private static abstract class DoctorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DoctorServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return doctor.Doctor.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("DoctorService");
    }
  }

  private static final class DoctorServiceFileDescriptorSupplier
      extends DoctorServiceBaseDescriptorSupplier {
    DoctorServiceFileDescriptorSupplier() {}
  }

  private static final class DoctorServiceMethodDescriptorSupplier
      extends DoctorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    DoctorServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (DoctorServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DoctorServiceFileDescriptorSupplier())
              .addMethod(getDoctorExistsMethod())
              .addMethod(getCheckAvailabilityMethod())
              .build();
        }
      }
    }
    return result;
  }
}
