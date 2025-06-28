package com.yourbank.exceptions;

import com.yourbank.exceptions.LibraryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class LibraryExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(LibraryException.class)
    public ResponseEntity<Object> handleLibraryException(LibraryException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // Check for specific exception types with ResponseStatus
        if (ex instanceof LibraryException.EntityNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex.getClass().isAnnotationPresent(ResponseStatus.class)) {
            status = ex.getClass().getAnnotation(ResponseStatus.class).value();
        }

        return new ResponseEntity<>(body, status);
    }
}
