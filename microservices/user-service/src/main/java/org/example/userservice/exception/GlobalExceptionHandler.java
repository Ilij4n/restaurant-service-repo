package org.example.userservice.exception;

import org.example.userservice.dto.MessageResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;
// Dunno how it does its magic yet, but it does so implicitly, so i do not really care
@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle validation exceptions (triggered by @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponseDto> handleValidation(MethodArgumentNotValidException ex) {
        // Collect all validation errors into a single message
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        MessageResponseDto response = new MessageResponseDto();
        response.setSuccess(false);
        response.setMessage(message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle our general runtime exceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageResponseDto> handleRuntime(RuntimeException ex) {
        MessageResponseDto response = new MessageResponseDto();
        response.setSuccess(false);
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponseDto> handleGeneral(Exception ex) {
        MessageResponseDto response = new MessageResponseDto();
        response.setSuccess(false);
        response.setMessage("An unexpected error occurred");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}