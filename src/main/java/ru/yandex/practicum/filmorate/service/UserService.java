package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.ResourceAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ResourceNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User get(long id) {
        if (!userStorage.contain(id)) {
            log.debug("Пользователь с id={} не найден", id);
            throw new ResourceNotExistException("Пользователь с id=" + id + " не найден");
        }
        return userStorage.get(id);
    }

    public User createUser(User user) {
        if (user.getId() != null && userStorage.contain(user.getId())) {
            log.info("Попытка повторно создать пользователя с id {} ", user.getId());
            throw new ResourceAlreadyExistException("Пользователь с таким id уже существует");
        }
        setEmptyName(user);
        return userStorage.create(user);
    }

    public User putUser(User user) {
        setEmptyName(user);
        if (user.getId() != null && !userStorage.contain(user.getId())) {
            log.info("Попытка обновить несуществующего пользователя");
            throw new ResourceAlreadyExistException("Попытка обновить несуществующего пользователя");
        }
        userStorage.put(user);
        return user;
    }

    public List<User> findAll() {
        List<User> users = userStorage.findAll();
        log.info("Общее количество пользователей {}", users.size());
        return users;
    }

    public Set<Long> addFriend(Long id, Long friendId) {
        if (!checkExist(id, friendId))
            throw new ResourceNotExistException(String.format(
                    "Пользователи должны существовать if=%d, friendId=%d", id, friendId
            ));
        userStorage.addFriend(id, friendId);
        return Set.of(id, friendId);
    }

    public Set<Long> removeFriend(Long id, Long friendId) {
        if (!checkExist(id, friendId))
            throw new ValidationException(String.format(
                    "Пользователи должны существовать if=%d, friendId=%d", id, friendId
            ));
        userStorage.removeFriend(id, friendId);
        return Set.of(id, friendId);
    }

    public List<User> findAllFriends(long id) {
        if (!checkExist(id)) {
            return Collections.emptyList();
        }
        return userStorage.findAllFriends(get(id).getFriends().keySet());
    }

    public List<User> findOverallFriends(long id, long friendId) {
        if (!checkExist(id, friendId))
            return Collections.emptyList();
        List<User> firstUserFriends = findAllFriends(id);
        if (firstUserFriends == null) {
            return Collections.emptyList();
        }
        return findAllFriends(friendId).stream()
                .filter(firstUserFriends::contains)
                .collect(Collectors.toList());
    }

    private boolean checkExist(Long... ids) {
        for (Long id : ids) {
            if (id == null || !userStorage.contain(id))
                return false;
        }
        return true;
    }

    private void setEmptyName(User user) {
        if (user.getName() == null || user.getName().isBlank())
            user.setName(user.getLogin());
    }
}
