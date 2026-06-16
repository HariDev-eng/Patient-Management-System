package com.pm.nurseservice.exception;

import java.util.UUID;

public class NurseNotFoundException
        extends RuntimeException {

    public NurseNotFoundException(
            UUID nurseId) {

        super(
                "Nurse not found: "
                        + nurseId
        );
    }
}