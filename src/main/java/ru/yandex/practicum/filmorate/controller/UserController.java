package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ResourceNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) {
        return userService.putUser(user);
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable long id) {
        return userService.get(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Set<Long> addFriend(@PathVariable(required = false) Long id,
                               @PathVariable(required = false) Long friendId) {
        if (id == null || friendId == null || id < 1 || friendId < 1) {
            throw new ResourceNotExistException(String.format(
                    "Id должны быть указаны и не быть больше 1 id=%d, otherId=%d ", id, friendId));
        }
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Set<Long> removeFriend(@PathVariable(required = false) Long id,
                                  @PathVariable(required = false) Long friendId) {
        if (id == null || friendId == null || id < 1 || friendId < 1) {
            throw new ResourceNotExistException(String.format(
                    "Id должны быть указаны и не быть больше 1 id=%d, otherId=%d ", id, friendId));
        }
        return userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findAllFriends(@PathVariable long id) {
        return userService.findAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findOverallFriends(@PathVariable(required = false) Long id,
                                         @PathVariable(required = false) Long otherId) {
        if (id == null || otherId == null || id < 1 || otherId < 1) {
            throw new ValidationException(String.format(
                    "Id должны быть указаны и не быть больше 1 id=%d, otherId=%d ", id, otherId));
        }
        return userService.findOverallFriends(id, otherId);
    }

}
