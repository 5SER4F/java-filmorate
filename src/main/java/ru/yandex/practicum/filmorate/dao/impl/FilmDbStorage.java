package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.RatingMPAStorage;
import ru.yandex.practicum.filmorate.exception.ResourceNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final RatingMPAStorage ratingMPAStorage;
    private final GenreStorage genreStorage;

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        film.setId(
                simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue()
        );
        addGenres(film);
        return film;
    }

    @Override
    public Film remove(Film film) {
        String sqlQuery = "DELETE FROM films WHERE id = ?";
        if (jdbcTemplate.update(sqlQuery, film.getId()) < 0) {
            log.info("Попытка удалить несуществующего пользователя с id={}", film.getId());
            throw new ResourceNotExistException("Попытка удалить несуществующего пользователя с id=" + film.getId());
        }
        return film;
    }

    @Override
    public Film put(Film film) {
        String sqlQuery = "MERGE INTO films (id, name, description, release_date, duration, rating_MPA_Id)" +
                "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sqlQuery,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        addGenres(film);
        return get(film.getId());
    }

    @Override
    public Film get(long id) {
        String sqlQuery = "SELECT * FROM films WHERE id=?";
        return jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id);
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "SELECT * FROM films";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    @Override
    public boolean contain(Long id) {
        if (id == null) {
            return false;
        }
        String sqlQuery = "SELECT EXISTS (SELECT * FROM films WHERE id=?)";
        return Boolean.TRUE.equals(
                jdbcTemplate.queryForObject(sqlQuery,
                        (rs, rowNum) -> rs.getBoolean(rs.getMetaData().getColumnName(1)),
                        id));
    }

    @Override
    public List<Film> findBestRatedFilms(int count) {
        String sqlQuery = "SELECT f.*, COUNT(fl.USER_ID) AS pop   " +
                "FROM FILMS AS f  " +
                "LEFT OUTER JOIN FILM_LIKE AS fl ON f.ID = fl.FILM_ID  " +
                "GROUP BY f.ID  " +
                "ORDER BY pop DESC  " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::makeFilm, count);
    }

    @Override
    public Map<String, Long> userLikeFilm(long id, long userId) {
        String sqlQuery = "MERGE INTO film_like (film_id, user_id)" +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId);
        return Map.of("filmId", id, "userId", userId);
    }

    @Override
    public Map<String, Long> userRemoveLikeFromFilm(long id, long userId) {
        String sqlQuery = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
        return Map.of("filmId", id, "userId", userId);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getLong("duration"),
                ratingMPAStorage.get(rs.getLong("rating_MPA_Id"))
        );
        film.setGenres(genreStorage.findAllGenreOfFilm(film.getId()));
        return film;
    }

    private void addGenres(Film film) {
        if (film.getGenres() == null) {
            return;
        }
        String sqlQuery = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());

        for (Genre genre : film.getGenres()) {
            sqlQuery = "MERGE INTO film_genre (genre_id, film_id)" +
                    "VALUES(?, ?)";
            jdbcTemplate.update(sqlQuery, genre.getId(), film.getId());
        }
    }

}
