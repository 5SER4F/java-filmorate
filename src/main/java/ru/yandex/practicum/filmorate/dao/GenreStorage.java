package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    Genre get(Long id);

    List<Genre> findAll();

    boolean contain(Long id);

    List<Genre> findAllGenreOfFilm(Long filmId);
}
