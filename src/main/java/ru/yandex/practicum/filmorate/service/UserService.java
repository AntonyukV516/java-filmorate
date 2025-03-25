package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(int id1, int id2) {
        User user1 = userStorage.getUsers().get(id1);
        User user2 = userStorage.getUsers().get(id2);
        user1.getFriends().add(id2);
        user2.getFriends().add(id1);

        log.info("Пользователь {} и {} теперь друзья", user1, user2);
        return user2;
    }

    public void deleteFriend(int id1, int id2) {
        User user1 = userStorage.getUsers().get(id1);
        User user2 = userStorage.getUsers().get(id2);
        user1.getFriends().remove(user2.getId());
        user2.getFriends().remove(user1.getId());
        log.info("Пользователи {} и {} больше не друзья", user1, user2);
    }

    public Set<User> getFriends(int id) {
        return userStorage.getUsers().get(id)
                .getFriends()
                .stream()
                .map(i -> userStorage.getUsers().get(i))
                .collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(int id1, int id2) {
        User user1 = userStorage.getUsers().get(id1);
        User user2 = userStorage.getUsers().get(id2);
        return user1.getFriends()
                .stream()
                .filter(u -> user2.getFriends().contains(u))
                .map(id -> userStorage.getUsers().get(id))
                .collect(Collectors.toSet());
    }

    public List<User> getUsers() {
        return new ArrayList<>(userStorage.getUsers().values());
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User newUser) {
        return userStorage.updateUser(newUser);
    }
}
