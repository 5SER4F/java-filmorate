package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage extends AbstractStorage<Film> {
    List<Film> findAll();

    boolean contain(Long id);

    List<Film> findBestRatedFilms(int count);

    Map<String, Long> userLikeFilm(long id, long userId);

    Map<String, Long> userRemoveLikeFromFilm(long id, long userId);
}
