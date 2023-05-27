package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

import java.util.List;

public interface FilmStorage extends AbstractStorage<Film> {
    List<Film> findAll();

    boolean contain(Long id);
}
