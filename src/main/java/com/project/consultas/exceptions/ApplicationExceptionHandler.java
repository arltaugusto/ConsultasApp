package com.project.consultas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApplicationErrors> handleInvalidCredentialsException(InvalidCredentialsException ex, WebRequest webRequest) {
        return clientException(ex, webRequest, "401", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ApplicationErrors> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest webRequest) {
        return clientException(ex, webRequest, "400", HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ApplicationErrors> clientException(Exception ex, WebRequest webRequest, String code, HttpStatus httpStatus) {
        ApplicationErrors errors = new ApplicationErrors(ex.toString(), code);
        errors.setDate(LocalDateTime.now());
        return new ResponseEntity<>(errors, httpStatus);
    }
}