package ru.yandex.practicum.filmorate.exception;

public class ResourceAlreadyExistException extends RuntimeException{
    public ResourceAlreadyExistException() {
    }

    public ResourceAlreadyExistException(String message) {
        super(message);
    }
}
