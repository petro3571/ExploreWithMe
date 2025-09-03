package ru.practicum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class Handler {
    private static final Logger log = LoggerFactory.getLogger(Handler.class);

    @ExceptionHandler(BadRequestException1.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(BadRequestException1 ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
    }
}