package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
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
        User user1 = userStorage.getUserById(id1);
        User user2 = userStorage.getUserById(id2);
        user1.getFriends().add(id2);
        updateUser(user1);

        log.info("Пользователь {} добавил в друзья пользователя {}", user1, user2);
        return user2;
    }

    public void deleteFriend(int id1, int id2) {
        User user1 = userStorage.getUserById(id1);
        user1.getFriends().remove(id2);
        updateUser(user1);
        log.info("Пользователь {} удалил из друзей пользователя {}", user1, userStorage.getUserById(id2));
    }

    public Set<User> getFriends(int id) {
        return userStorage.getUserById(id)
                .getFriends()
                .stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(int id1, int id2) {
        User user1 = userStorage.getUserById(id1);
        User user2 = userStorage.getUserById(id2);

        Set<Integer> commonFriendIds = new HashSet<>(user1.getFriends());
        commonFriendIds.retainAll(user2.getFriends());

        return commonFriendIds.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toSet());
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User newUser) {
        return userStorage.updateUser(newUser);
    }
}

