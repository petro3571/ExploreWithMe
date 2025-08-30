package ru.practicum.exceptions;

public class RequestConditionsException extends RuntimeException {
    public RequestConditionsException(String message) {
        super(message);
    }
}