package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exception.ResourceAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public Film get(long id) {
        if (!filmStorage.contain(id)) {
            throw new ResourceAlreadyExistException("Фильм с таким id=" + id + " не существует");
        }
        return filmStorage.get(id);
    }

    public Film createFilm(Film film) {
        if (filmStorage.contain(film.getId())) {
            log.info("Попытка повторно создать фильм с id {} ", film.getId());
            throw new ResourceAlreadyExistException("Фильм с таким id=" + film.getId() + " уже существует");
        }
        if (film.getReleaseDate().isBefore(Film.EARLIEST_DATE)) {
            throw new ValidationException("Фильм не может иметь дату релиза раньше чем "
                    + Film.EARLIEST_DATE);
        }
        log.info("Создан фильм с id {}, {}", film.getId(), film);
        return filmStorage.create(film);
    }

    public Film putFilm(Film film) {
        if (film.getId() != null && !filmStorage.contain(film.getId())) {
            log.info("Попытка обновить несуществующий фильм");
            throw new ResourceAlreadyExistException("Попытка обновить несуществующий фильм");
        }
        return filmStorage.put(film);
    }

    public List<Film> findAll() {
        List<Film> films = filmStorage.findAll();
        log.debug("Общее количество фильмов {}", films.size());
        return films;
    }

    public Map<String, Long> userLikeFilm(long id, long userId) {
        return filmStorage.userLikeFilm(id, userId);
    }

    public Map<String, Long> userRemoveLikeFromFilm(Long id, Long userId) {
        return filmStorage.userRemoveLikeFromFilm(id, userId);
    }

    public List<Film> findBestRatedFilms(int count) {
        return filmStorage.findBestRatedFilms(count);
    }

}
