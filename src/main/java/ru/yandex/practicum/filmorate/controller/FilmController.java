package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.ResourceAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static int idCounter = 0;
    Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            log.debug("Попытка повторно создать фильм с id {} ", film.getId());
            throw new ResourceAlreadyExistException("Фильм с таким id уже существует");
        }
        validation(film);
        if (film.getId() == 0)
            film.setId(getNewId());
        log.debug("Создан фильм с id {}", film.getId());
        put(film);
        return films.get(film.getId());
    }

    @PutMapping
    public Film putFilm(@Valid @RequestBody Film film) {
        validation(film);
        if (film.getId() != 0 && !films.containsKey(film.getId()))
            throw new ResourceAlreadyExistException("Попытка обновить несуществующий фильм");
        if (film.getId() == 0)
            film.setId(getNewId());
        log.debug("Добавлен фильм с id {}", film.getId());
        put(film);
        return film;
    }

    @GetMapping
    public List<Film> findAll() {
        log.debug("Общее количество фильмов {}", films.size());
        return films.values().stream()
                .collect(Collectors.toList());
    }

    private void validation(Film film) {
        if (film.getReleaseDate().isBefore(Film.EARLIEST_DATE))
            failedValidation("Дата релиза — не раньше 28 декабря 1895");
        if (film.getDuration() <= 0)
            failedValidation("Продолжительность фильма должна быть положительной");
    }

    private void put(Film film) {
        films.put(film.getId(), film);
    }

    private void failedValidation(String message) {
        log.debug(message);
        throw new ValidationException(message);
    }

    private static int getNewId() {
        return ++idCounter;
    }
}
