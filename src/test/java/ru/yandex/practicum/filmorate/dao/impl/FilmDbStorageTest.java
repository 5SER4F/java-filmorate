package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private static Film film;

    @BeforeEach
    void newFilm() {
        film = new Film(null, "name", "description",
                LocalDate.now().minusMonths(10),
                100, new RatingMPA(1L, "G"));
    }

    @Test
    void testCreateGetRemove() {
        assertEquals(film, filmDbStorage.create(film));

        film.setId(1L);

        assertEquals(film, filmDbStorage.get(1));

        assertEquals(film, filmDbStorage.remove(film));
    }
}