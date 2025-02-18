package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Map<Integer, User> getUsers() {
        return users;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        users.put(user.getId(), user);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        log.info("Добавлен пользователь : {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        User oldUser = users.get(newUser.getId());
        oldUser.setId(newUser.getId());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(newUser.getName());
        oldUser.setBirthday(newUser.getBirthday());
        log.info("Старый пользователь после обновления : {}", oldUser);
        return oldUser;
    }
}
