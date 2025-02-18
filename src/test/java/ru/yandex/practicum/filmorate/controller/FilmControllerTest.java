package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void init() {
        filmController = new FilmController();
    }

    @Test
    @DisplayName("Тест на добавление и вывод всех фильмов")
    void addAndGetFilms() {
        Map<Integer,Film> expectedFilms = Map.of(1, new Film(1, "name", "description",
                LocalDate.of(2000, 10, 28), 100));


        filmController.addFilm(new Film(1, "name", "description",
                LocalDate.of(2000, 10, 28), 100));
        Map<Integer, Film> actualFilms = filmController.getFilms();

        assertEquals(expectedFilms, actualFilms);
    }

    @Test
    @DisplayName("Тест на обновление фильма")
    void updateFilm() {
        Film oldFilm = filmController.addFilm(new Film(1, "name", "description",
                LocalDate.of(2000, 10, 28), 100));
        Film newFilm = new Film(1, "test", "description",
                LocalDate.of(2000, 10, 28), 100);

        Film updatedFilm = filmController.updateFilm(newFilm);

        assertEquals(oldFilm.getName(), updatedFilm.getName());
    }
}