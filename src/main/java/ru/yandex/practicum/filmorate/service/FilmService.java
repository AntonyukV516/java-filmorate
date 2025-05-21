package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    UserStorage userStorage;
    FilmStorage filmStorage;

    @Autowired
    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public User addLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        film.getLikedByUserIds().add(user.getId());
        updateFilm(film);
        log.info("Пользователь {} добавил лайк фильму {} ", user, film);
        return user;
    }

    public User deleteLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        film.getLikedByUserIds().remove(user.getId());
        updateFilm(film);
        log.info("Пользователь {} удалил лайк фильму {} ", user, film);
        return user;
    }

    public List<Film> getPopularFilms(int maxSize) {
        return filmStorage.getFilms()
                .stream()
                .sorted(Comparator.comparing(Film::getLikesSize).reversed())
                .limit(maxSize)
                .toList();
    }

    public List<Genre> getGenres() {
        return filmStorage.getGenres();
    }

    public Genre getGenreById(int id) {
        return filmStorage.getGenreById(id);
    }

    public List<Mpa> getMpa() {
        return filmStorage.getMpa();
    }

    public Mpa getMpaById(int id) {
        return filmStorage.getMpaById(id);
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }
}
