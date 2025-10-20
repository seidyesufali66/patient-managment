package com.pm.patientservice.exception;
import com.pm.patientservice.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import static org.springframework.http.ResponseEntity.*;
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " :- " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");
        return status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(errorMessage, request.getRequestURI()));
    }
    @ExceptionHandler(EmailAlreadyExitsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExitsException(EmailAlreadyExitsException ex, HttpServletRequest request) {
      log.warn("Email already exists exception: {}", ex.getMessage());
        return status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage(), request.getRequestURI()));
    }
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePatientNotFoundException(PatientNotFoundException ex, HttpServletRequest request) {
        log.warn("Patient not found exception: {}", ex.getMessage());
        return status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage(), request.getRequestURI()));

    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
       ErrorResponse errorMessage= new ErrorResponse("Unexpected error occured", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
    }



}
