package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

import java.util.Collection;
import java.util.List;

public interface UserStorage extends AbstractStorage<User> {
    List<User> findAllFriends(Collection<Long> id);

    List<User> findAll();

    boolean contain(Long id);

}
