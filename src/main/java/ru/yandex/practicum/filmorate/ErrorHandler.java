package ru.yandex.practicum.filmorate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleInvalidId(final IllegalArgumentException e){
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidationException(final ValidationException e){
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST
        );
    }
}
