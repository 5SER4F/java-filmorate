package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ResourceAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private static int idCounter = 0;
    Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            log.debug("Попытка повторно создать пользователя с id {} ", user.getId());
            throw new ResourceAlreadyExistException("Пользователь с таким id уже существует");
        }
        validation(user);
        if (user.getId() == 0)
            user.setId(getNewId());
        log.debug("Создан пользователь с id {}", user.getId());
        put(user);
        return user;
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) {
        validation(user);
        if (user.getId() != 0 && !users.containsKey(user.getId()))
            throw new ResourceAlreadyExistException("Попытка обновить несуществующего пользователя");
        if (user.getId() == 0)
            user.setId(getNewId());
        log.debug("Добавлен пользователь с id {}", user.getId());
        put(user);
        return users.get(user.getId());
    }

    @GetMapping
    public List<User> findAll() {
        log.debug("Общее количество пользователей {}", users.size());
        return users.values().stream()
                .collect(Collectors.toList());
    }

    private void validation(User user) {
        if (user.getName() == null || user.getName().isBlank())
            user.setName(user.getLogin());
        if (user.getBirthday().isAfter(LocalDate.now()))
            failedValidation("Некорректная дата рождения");
    }

    private void put(User user) {
        users.put(user.getId(), user);
    }

    private void failedValidation(String message) {
        log.debug(message);
        throw new ValidationException(message);
    }

    private static int getNewId() {
        return ++idCounter;
    }
}
