package ru.yandex.practicum.filmorate.exception;

public class ResourceNotExistException extends RuntimeException {
    public ResourceNotExistException() {
    }

    public ResourceNotExistException(String message) {
        super(message);
    }
}
