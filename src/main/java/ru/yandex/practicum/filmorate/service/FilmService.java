package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;

@Service
@Data
@Slf4j
@Validated
public class FilmService {
    UserStorage userStorage;
    FilmStorage filmStorage;

    @Autowired
    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public User addLike(int filmId, int userId) {
        Film film = filmStorage.getFilms().get(filmId);
        User user = userStorage.getUsers().get(userId);
        film.getLikes().add(user.getId());
        log.info("Пользователь {} добавил лайк фильму {} ", user, film);
        return user;
    }

    public User deleteLike(int filmId, int userId) {
        Film film = filmStorage.getFilms().get(filmId);
        User user = userStorage.getUsers().get(userId);
        film.getLikes().remove(user.getId());
        log.info("Пользователь {} удалил лайк фильму {} ", user, film);
        return user;
    }

    public List<Film> getPopularFilms(@NotNull FilmStorage filmStorage, int maxSize) {
        return filmStorage.getFilms()
                .values()
                .stream()
                .sorted(Comparator.comparing(Film::getLikesSize).reversed())
                .limit(maxSize)
                .toList();
    }
}
