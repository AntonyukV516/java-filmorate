package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friends(new HashSet<>())
                .build();
    }

    @Override
    public List<User> getUsers() {
        String usersSql = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(usersSql, this::mapRowToUser);
        if (users.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Integer, Set<Integer>> friendsByUserId = getAllFriends();

        users.forEach(user -> {
            Set<Integer> friends = friendsByUserId.getOrDefault(user.getId(), Collections.emptySet());
            user.setFriends(friends);
        });
        return users;
    }

    @Override
    public User getUserById(Integer id) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
            if (user != null) {
                user.setFriends(getFriends(id));
            }
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id  " + id + " не найден");
        }
    }

    @Override
    public User addUser(User user) {
        String sqlQuery = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        Integer generatedId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        user.setId(generatedId);

        log.info("Добавлен пользователь : {}", user);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        log.info("Пришел запрос на обновление newUser : {}", newUser);
        String sqlQuery = " UPDATE users SET" +
                " email = ?, login = ?, name = ?, birthday = ?" +
                " WHERE id = ?";
        int updatedRows = jdbcTemplate.update(sqlQuery,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday(),
                newUser.getId());
        if (updatedRows == 0) {
            throw new NullPointerException("Id не найден");
        }
        updateFriends(newUser);
        return newUser;
    }

    private void updateFriends(User user) {
        jdbcTemplate.update("DELETE FROM user_friends WHERE user_id = ?", user.getId());
        String sql = "INSERT INTO user_friends (user_id, friend_id) VALUES (?, ?)";
        List<Object[]> batchArgs = user.getFriends().stream()
                .map(friendId -> new Object[]{user.getId(), friendId})
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private Set<Integer> getFriends(Integer id) {
        String friendsSql = "SELECT friend_id FROM user_friends WHERE user_id = ?";
        return new HashSet<>(
                jdbcTemplate.queryForList(friendsSql, Integer.class, id)
        );
    }

    private Map<Integer, Set<Integer>> getAllFriends() {
        String sql = "SELECT user_id, friend_id FROM user_friends";
        return jdbcTemplate.query(sql, rs -> {
            Map<Integer, Set<Integer>> map = new HashMap<>();
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                int friendId = rs.getInt("friend_id");
                map.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
            }
            return map;
        });
    }
}

