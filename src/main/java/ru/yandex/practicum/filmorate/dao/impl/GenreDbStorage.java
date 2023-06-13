package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre get(Long id) {
        String sqlQuery = "SELECT * FROM genres WHERE id=?";
        return jdbcTemplate.queryForObject(sqlQuery,
                this::makeGenre, id);
    }

    @Override
    public List<Genre> findAll() {
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, this::makeGenre);
    }

    @Override
    public boolean contain(Long id) {
        if (id == null) {
            return false;
        }
        String sqlQuery = "SELECT EXISTS (SELECT * FROM genres WHERE id=?)";
        return Boolean.TRUE.equals(
                jdbcTemplate.queryForObject(sqlQuery,
                        (rs, rowNum) -> rs.getBoolean(rs.getMetaData().getColumnName(1)),
                        id)
        );
    }

    @Override
    public List<Genre> findAllGenreOfFilm(Long filmId) {
        String sqlQuery = "SELECT * FROM genres AS g    " +
                "WHERE g.id IN (    " +
                "    SELECT fg.genre_id    " +
                "    FROM film_genre AS fg    " +
                "    WHERE fg.film_id = ?    " +
                ")";
        return jdbcTemplate.query(sqlQuery, this::makeGenre, filmId);
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getLong("id"),
                rs.getString("name"));
    }
}
