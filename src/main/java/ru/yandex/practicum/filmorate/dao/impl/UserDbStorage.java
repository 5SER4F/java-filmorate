package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.ResourceNotExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        user.setId(
                simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue()
        );
        return user;
    }

    @Override
    public User remove(User user) {
        String sqlQuery = "DELETE FROM users WHERE id = ?";
        if (jdbcTemplate.update(sqlQuery, user.getId()) < 0) {
            log.info("Попытка удалить несуществующего пользователя с id={}", user.getId());
            throw new ResourceNotExistException("Попытка удалить несуществующего пользователя с id=" + user.getId());
        }
        return user;
    }

    @Override
    public User put(User user) {
        String sqlQuery = "MERGE INTO users (id, login, name, email, birthdate)" +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sqlQuery,
                user.getId(),
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday());
        return user;
    }

    @Override
    public User get(long id) {
        if (id == 0) {
            throw new ResourceNotExistException("Id должен быть задан");
        }
        String sqlQuery = "SELECT * FROM users WHERE id=?";
        User user = jdbcTemplate.queryForObject(sqlQuery, this::makeUser, id);
        if (user == null) {
            log.debug("Пользователь с id={} не найден", id);
            throw new ResourceNotExistException("Пользователь с id=" + id + " не найден");
        }
        user.setFriends(findFriendsById(id));
        return user;
    }

    @Override
    public List<User> findAllFriends(Collection<Long> ids) {
        return ids.stream()
                .map(this::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, this::makeUser);
    }

    @Override
    public boolean contain(Long id) {
        if (id == null) {
            return false;
        }
        String sqlQuery = "SELECT EXISTS (SELECT * FROM users WHERE id=?)";
        return Boolean.TRUE.equals(
                jdbcTemplate.queryForObject(sqlQuery,
                        (rs, rowNum) -> rs.getBoolean(rs.getMetaData().getColumnName(1)),
                        id)
        );
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        String sqlQuery = "MERGE INTO friends (initiator_id, target_id)" +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public void removeFriend(Long id, Long friendId) {
        String sqlQuery = "DELETE FROM friends WHERE initiator_id=? AND target_id=?";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthdate").toLocalDate()
        );
    }

    private Map<Long, Boolean> findFriendsById(Long id) {
        String sqlQuery = "SELECT target_id, confirm FROM friends WHERE initiator_id=?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        Map<Long, Boolean> friends = new HashMap<>();
        while (sqlRowSet.next()) {
            friends.put(sqlRowSet.getLong("target_id"),
                    sqlRowSet.getBoolean("confirm"));
        }
        return friends;
    }
}
