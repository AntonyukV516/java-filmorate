package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    List<Film> getFilms();

    Film getFilmById(Integer id);

    Film updateFilm(Film newFilm);

    List<Genre> getGenres();

    Genre getGenreById(int id);

    List<Mpa> getMpa();

    Mpa getMpaById(int id);

}