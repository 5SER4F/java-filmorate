package ru.yandex.practicum.filmorate.storage;

public interface AbstractStorage<T> {
    T create(T data);

    T remove(T data);

    T put(T data);

    T get(long id);
}
