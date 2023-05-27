package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ResourceNotExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        if (users.containsValue(user)) {
            log.debug("Попытка повторно создать пользователя с id {} ", user.getId());
            throw new ResourceAlreadyExistException("Пользователь с таким id уже существует");
        }
        return add(user);
    }

    @Override
    public User remove(User user) {
        return users.remove(user);
    }

    @Override
    public User put(User user) {
        return add(user);
    }

    @Override
    public User get(long id) {
        if (!users.containsKey(id)) {
            log.debug("Попытка получить не существующего пользователя с id={}", id);
            throw new ResourceNotExistException(String.format("Пользователь с id=%d не существуетю", id));
        }
        return users.get(id);
    }

    @Override
    public List<User> findAllFriends(Collection<Long> ids) {
        return users.values().stream()
                .filter(user -> ids.contains(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean contain(Long id) {
        return users.containsKey(id);
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(users.values());
    }

    private User add(User user) {
        users.put(user.getId(), user);
        return user;
    }
}
