package ru.yandex.practicum.filmorate.storage;

public interface AbstractStorage<T> {
    T create(T T);

    T remove(T T);

    T put(T T);

    T get(long id);
}
