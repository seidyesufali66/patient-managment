package com.pm.patientservice.exception;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(String message, String id)
    {
        super(message);
    }
}
