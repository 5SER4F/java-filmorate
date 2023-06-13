package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.RatingMPAStorage;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class RatingMPADbStorage implements RatingMPAStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public RatingMPA get(Long id) {
        String sqlQuery = "SELECT * FROM ratings_MPA WHERE id=?";
        return jdbcTemplate.queryForObject(sqlQuery,
                this::makeMPA, id);
    }

    @Override
    public List<RatingMPA> findAll() {
        String sqlQuery = "SELECT * FROM ratings_MPA";
        return jdbcTemplate.query(sqlQuery, this::makeMPA);
    }

    @Override
    public boolean contain(Long id) {
        if (id == null) {
            return false;
        }
        String sqlQuery = "SELECT EXISTS (SELECT * FROM ratings_MPA WHERE id=?)";
        return Boolean.TRUE.equals(
                jdbcTemplate.queryForObject(sqlQuery,
                        (rs, rowNum) -> rs.getBoolean(rs.getMetaData().getColumnName(1)),
                        id)
        );
    }

    private RatingMPA makeMPA(ResultSet rs, int rowNum) throws SQLException {
        return new RatingMPA(rs.getLong("id"),
                rs.getString("description"));
    }
}
