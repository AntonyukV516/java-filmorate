package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Data
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private static Integer globalId = 0;

    private static Integer getNextId() {
        return ++globalId;
    }

    @Override
    public Map<Integer, User> getUsers() {
        return users;
    }

    @Override
    public User addUser(User user) {
        user.setId(getNextId());
        users.put(user.getId() - 1, user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.info("Добавлен пользователь : {}", user);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        User oldUser;
        Optional<User> optOldUser = Optional.ofNullable(users.get(newUser.getId() - 1));
        if (optOldUser.isPresent()) {
            oldUser = optOldUser.get();
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            Optional<String> optName = Optional.ofNullable(newUser.getName());
            if (optName.isPresent()) {
                oldUser.setName(newUser.getName());
            } else {
                oldUser.setName(newUser.getLogin());
            }
            oldUser.setBirthday(newUser.getBirthday());
            log.info("Старый пользователь после обновления : {}", oldUser);
            return oldUser;
        }
        throw new NullPointerException("Нельзя обновлять не созданного пользователя");
    }
}
