package ru.yandex.practicum.filmorate.exception;

public class SQLAccessException extends RuntimeException {
    public SQLAccessException() {
    }

    public SQLAccessException(String message) {
        super(message);
    }
}
