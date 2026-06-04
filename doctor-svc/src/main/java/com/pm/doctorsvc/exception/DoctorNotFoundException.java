package com.pm.doctorsvc.exception;

public class DoctorNotFoundException
        extends RuntimeException {

    public DoctorNotFoundException(String message) {
        super(message);
    }
}