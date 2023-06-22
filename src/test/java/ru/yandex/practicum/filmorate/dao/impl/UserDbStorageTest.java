package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userStorage;
    private static User user;

    @BeforeEach
    void newUser() {
        user = new User(null, "email@gmail.com", "login", "name",
                LocalDate.now().minusMonths(10));
    }

    @Test
    void testCreateGetRemove() {
        assertEquals(user, userStorage.create(user));

        user.setId(1L);

        assertEquals(user, userStorage.get(1));

        assertEquals(user, userStorage.remove(user));
    }

}
