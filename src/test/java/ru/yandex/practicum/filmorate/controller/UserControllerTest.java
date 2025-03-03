package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void init() {
        UserService userService = new UserService();
        userController = new UserController(userService);
    }

    @Test
    @DisplayName("Тест на добавление и вывод всех пользователей")
    void addAndGetUsers() {
        Map<Integer, User> expectedUsers = Map.of(0, new User(1, "email@test.ru", "login", "name",
                LocalDate.of(2000, 10, 28)));


        userController.addUser(new User(0, "email@test.ru", "login", "name",
                LocalDate.of(2000, 10, 28)));
        Map<Integer, User> actualUsers = userController.getUsers();
        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    @DisplayName("Тест на обновление пользователя")
    void updateUser() {
        User oldUser = userController.addUser(new User(0, "email@test.ru", "login", "name",
                LocalDate.of(2000, 10, 28)));
        User newUser = new User(oldUser.getId(), "email@test.ru", "login", "test",
                LocalDate.of(2000, 10, 28));

        User updatedUser = userController.updateUser(newUser);

        assertEquals(oldUser.getName(), updatedUser.getName());
    }
}