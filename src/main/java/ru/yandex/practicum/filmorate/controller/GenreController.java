package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.exception.ResourceNotExistException;
import ru.yandex.practicum.filmorate.model.Genre;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreStorage genreStorage;

    @GetMapping
    public List<Genre> findAll() {
        return genreStorage.findAll();
    }

    @GetMapping("/{id}")
    public Genre get(@PathVariable @NotNull Long id) {
        if (!genreStorage.contain(id)) {
            throw new ResourceNotExistException("Не существует жанра с id = " + id);
        }
        return genreStorage.get(id);
    }

}
