package ru.practicum.ewm.main.exception;

public class Conflict409Exception extends RuntimeException {
    public Conflict409Exception(String message) {
        super(message);
    }
}
