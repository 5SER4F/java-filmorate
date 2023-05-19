package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ResourceNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        if (films.containsKey(film.getId()))
            throw new ResourceAlreadyExistException("Такой фильм уже создан id=" + film.getId());
        return add(film);
    }

    @Override
    public Film remove(Film film) {
        return films.remove(film);
    }

    @Override
    public Film put(Film film) {
        if (film.getReleaseDate().isBefore(Film.EARLIEST_DATE))
            throw new ValidationException("Фильм не может иметь дату релиза раньше чем "
                    + Film.EARLIEST_DATE);
        return add(film);
    }

    @Override
    public Film get(long id) {
        if (!films.containsKey(id)) {
            log.debug("Попытка получить не существующий фильм с id={}", id);
            throw new ResourceNotExistException(String.format("Фильм с id=%d не существуетю", id));
        }
        return films.get(id);
    }

    @Override
    public List<Film> findAll() {
        return List.copyOf(films.values());
    }

    @Override
    public boolean contain(Long id) {
        if (id == null)
            return false;
        return films.containsKey(id);
    }

    private Film add(Film film) {
        films.put(film.getId(), film);
        return film;
    }

}
