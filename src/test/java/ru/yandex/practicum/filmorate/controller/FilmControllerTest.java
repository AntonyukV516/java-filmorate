package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void init() {
        FilmService filmService = new FilmService();
        filmController = new FilmController(filmService);
    }

    @Test
    @DisplayName("Тест на добавление и вывод всех фильмов")
    void addAndGetFilms() {
        Map<Integer, Film> expectedFilms = Map.of(1, new Film(1, "name", "description",
                LocalDate.of(2000, 10, 28), 100));

        filmController.addFilm(new Film(0, "name", "description",
                LocalDate.of(2000, 10, 28), 100));
        Map<Integer, Film> actualFilms = filmController.getFilms();

        assertEquals(expectedFilms, actualFilms);
    }

    @Test
    @DisplayName("Тест на обновление фильма")
    void updateFilm() {
        Film oldFilm = filmController.addFilm(new Film(1, "name", "description",
                LocalDate.of(2000, 10, 28), 100));
        Film newFilm = new Film(oldFilm.getId(), "test", "description",
                LocalDate.of(2000, 10, 28), 100);

        Film updatedFilm = filmController.updateFilm(newFilm);

        assertEquals(oldFilm.getName(), updatedFilm.getName());
    }

    @Test
    @DisplayName("Тест на валидацию фильма по времени релиза")
    void validateTest() {
        Assertions.assertThrows(ValidationException.class, () -> {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();
            Optional<ConstraintViolation<Film>> violation = validator
                    .validate(new Film(1, "name", "description",
                            LocalDate.of(1700, 10, 28), 100))
                    .stream().findFirst();
            if (violation.isPresent()) {
                throw new ValidationException(violation.get().getMessage());
            }
        });
    }
}