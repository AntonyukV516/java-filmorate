package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void init() {
        userController = new UserController();
    }

    @Test
    @DisplayName("Тест на добавление и вывод всех пользователей")
    void addAndGetUsers() {
        List<User> expectedUsers = List.of(new User(0, "email@test.ru", "login", "name",
                LocalDate.of(2000, 10, 28)));


        userController.addUser(new User(0, "email@test.ru", "login", "name",
                LocalDate.of(2000, 10, 28)));
        List<User> actualUsers = userController.getUsers();
        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    @DisplayName("Тест на обновление пользователя")
    void updateUser() {
        User oldUser = userController.addUser(new User(0, "email@test.ru", "login", "name",
                LocalDate.of(2000, 10, 28)));
        User newUser = new User(0, "email@test.ru", "login", "test",
                LocalDate.of(2000, 10, 28));

        User updatedUser = userController.updateUser(newUser);

        assertEquals(oldUser.getName(), updatedUser.getName());
    }
}