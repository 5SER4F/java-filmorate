package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ResourceNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film putFilm(@Valid @RequestBody Film film) {
        return filmService.putFilm(film);
    }

    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film get(@PathVariable long id) {
        return filmService.get(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Map<String, Long> userLikeFilm(@PathVariable @Positive long id,
                                          @PathVariable @Positive long userId) {
        if (id <= 0 || userId <= 0) {
            throw new ResourceNotExistException();
        }
        return filmService.userLikeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Map<String, Long> userRemoveLikeFromFilm(@PathVariable @Positive long id,
                                                    @PathVariable @Positive long userId) {
        if (id <= 0 || userId <= 0) {
            throw new ResourceNotExistException();
        }
        return filmService.userRemoveLikeFromFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> findBestRatedFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.findBestRatedFilms(count);
    }
}
