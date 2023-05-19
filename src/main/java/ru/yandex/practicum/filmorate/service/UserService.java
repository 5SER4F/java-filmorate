package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ResourceNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private static long idCounter = 0;
    private final UserStorage userStorage;


    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User get(long id) {
        return userStorage.get(id);
    }

    public User createUser(User user) {
        if (userStorage.contain(user.getId())) {
            log.debug("Попытка повторно создать пользователя с id {} ", user.getId());
            throw new ResourceAlreadyExistException("Пользователь с таким id уже существует");
        }
        setEmptyName(user);
        if (user.getId() == 0)
            user.setId(getNewId());
        log.debug("Создан пользователь с id {}", user.getId());
        userStorage.create(user);
        return user;
    }

    public User putUser(User user) {
        setEmptyName(user);
        if (user.getId() != 0 && !userStorage.contain(user.getId())) {
            log.debug("Попытка обновить несуществующего пользователя");
            throw new ResourceAlreadyExistException("Попытка обновить несуществующего пользователя");
        }
        if (user.getId() == 0)
            user.setId(getNewId());
        log.debug("Добавлен пользователь с id {}", user.getId());
        userStorage.create(user);
        return userStorage.get(user.getId());
    }

    public List<User> findAll() {
        List<User> users = userStorage.findAll();
        log.debug("Общее количество пользователей {}", users.size());
        return users;
    }

    public Set<Long> addFriend(Long id, Long friendId) {
        if (!checkExist(id, friendId))
            throw new ResourceNotExistException(String.format(
                    "Пользователи должны существовать if=%d, friendId=%d", id, friendId
            ));
        userStorage.get(id).addFriend(friendId);
        userStorage.get(friendId).addFriend(id);
        return Set.of(id, friendId);
    }

    public Set<Long> removeFriend(Long id, Long friendId) {
        if (!checkExist(id, friendId))
            throw new ValidationException(String.format(
                    "Пользователи должны существовать if=%d, friendId=%d", id, friendId
            ));
        userStorage.get(id).removeFriend(friendId);
        userStorage.get(friendId).removeFriend(id);
        return Set.of(id, friendId);
    }

    public List<User> findAllFriends(long id) {
        if (!checkExist(id))
            return Collections.emptyList();
        return userStorage.findAllFriends(
                userStorage.get(id).getFriends()
        );
    }

    public List<User> findOverallFriends(long id, long friendId) {
        if (!checkExist(id, friendId))
            return Collections.emptyList();
        List<User> firstUserFriends = findAllFriends(id);
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

    private static long getNewId() {
        return ++idCounter;
    }

    private void setEmptyName(User user) {
        if (user.getName() == null || user.getName().isBlank())
            user.setName(user.getLogin());
    }
}
