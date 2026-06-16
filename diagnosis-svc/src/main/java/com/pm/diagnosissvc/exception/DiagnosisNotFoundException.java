package com.pm.diagnosissvc.exception;

import java.util.UUID;

public class DiagnosisNotFoundException
        extends RuntimeException {

    public DiagnosisNotFoundException(
            UUID diagnosisId) {

        super(
                "Diagnosis not found with id: "
                        + diagnosisId
        );
    }
}