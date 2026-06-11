package com.pm.patientservice.grpc;

import com.pm.patientservice.repository.PatientRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import patient.PatientExistsRequest;
import patient.PatientExistsResponse;
import patient.PatientServiceGrpc;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class PatientGrpcService
        extends PatientServiceGrpc.PatientServiceImplBase {

    private final PatientRepository repository;

    @Override
    public void patientExists(
            PatientExistsRequest request,
            StreamObserver<PatientExistsResponse> responseObserver) {

        boolean exists =
                repository.existsById(
                        UUID.fromString(
                                request.getPatientId()));

        PatientExistsResponse response =
                PatientExistsResponse.newBuilder()
                        .setExists(exists)
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}