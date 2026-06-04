package com.pm.doctorsvc.exception;

public class LicenseAlreadyExistsException
        extends RuntimeException {

    public LicenseAlreadyExistsException(String message) {
        super(message);
    }
}
