package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ResourceNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private static long idCounter = 0;
    private final FilmStorage filmStorage;


    @Autowired
    FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film get(long id) {
        return filmStorage.get(id);
    }

    public Film createFilm(Film film) {
        if (filmStorage.contain(film.getId())) {
            log.debug("Попытка повторно создать фильм с id {} ", film.getId());
            throw new ResourceAlreadyExistException("Фильм с таким id=" + film.getId() + " уже существует");
        }
        if (film.getReleaseDate().isBefore(Film.EARLIEST_DATE))
            throw new ValidationException("Фильм не может иметь дату релиза раньше чем "
                    + Film.EARLIEST_DATE);
        if (film.getId() == 0)
            film.setId(getNewId());
        log.debug("Создан фильм с id {}", film.getId());
        return filmStorage.create(film);
    }

    public Film putFilm(Film film) {
        if (film.getId() != 0 && !filmStorage.contain(film.getId()))
            throw new ResourceAlreadyExistException("Попытка обновить несуществующий фильм");
        if (film.getId() == 0) {
            film.setId(getNewId());
        }
        log.debug("Добавлен фильм с id {}", film.getId());
        return filmStorage.put(film);
    }

    public List<Film> findAll() {
        List<Film> films = filmStorage.findAll();
        log.debug("Общее количество фильмов {}", films.size());
        return films;
    }

    public Map<String, Long> userLikeFilm(long id, long userId) {
        if (!filmStorage.get(id).addLike(userId)) {
            throw new ResourceAlreadyExistException(String.format("У фильма id=%d уже есть лайк юзера id=%d", id, userId));
        }
        return Map.of("filmId", id, "userId", userId);
    }

    public Map<String, Long> userRemoveLikeFromFilm(long id, long userId) {
        if (!filmStorage.get(id).removeLike(userId)) {
            throw new ResourceNotExistException(String.format("У фильма id=%d нет лайка юзера id=%d", id, userId));
        }
        return Map.of("filmId", id, "userId", userId);
    }

    public List<Film> findBestRatedFilms(final int count) {
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size()).limit(count)
                .collect(Collectors.toList());
    }


    private static long getNewId() {
        return ++idCounter;
    }

}
