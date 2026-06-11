package com.pm.appointmentservice.grpc;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import patient.PatientExistsRequest;
import patient.PatientExistsResponse;
import patient.PatientServiceGrpc;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientGrpcClient {

    @GrpcClient("patient-service")
    private PatientServiceGrpc.PatientServiceBlockingStub patientStub;

    public boolean patientExists(UUID patientId) {

        PatientExistsRequest request =
                PatientExistsRequest.newBuilder()
                        .setPatientId(patientId.toString())
                        .build();

        PatientExistsResponse response =
                patientStub.patientExists(request);

        return response.getExists();
    }
}