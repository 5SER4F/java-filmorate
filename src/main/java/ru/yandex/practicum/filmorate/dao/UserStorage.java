package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage extends AbstractStorage<User> {
    List<User> findAllFriends(Collection<Long> id);

    List<User> findAll();

    boolean contain(Long id);

    void addFriend(Long id, Long friendId);

    void removeFriend(Long id, Long friendId);

}
