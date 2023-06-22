package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.RatingMPAStorage;
import ru.yandex.practicum.filmorate.exception.ResourceNotExistException;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final RatingMPAStorage mpaStorage;

    @GetMapping
    public List<RatingMPA> findAll() {
        return mpaStorage.findAll();
    }

    @GetMapping("/{id}")
    public RatingMPA get(@PathVariable @NotNull Long id) {
        if (!mpaStorage.contain(id)) {
            throw new ResourceNotExistException("Не существует рейтинга MPA с id = " + id);
        }
        return mpaStorage.get(id);
    }
}
