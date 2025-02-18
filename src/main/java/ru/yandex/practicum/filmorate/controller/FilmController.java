package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Map<Integer, Film> getFilms() {
        return films;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        films.put(film.getId(), film);
        log.info("Добавлен фильм : {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        oldFilm.setId(newFilm.getId());
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        log.info("Старый фильм после обновления : {}", oldFilm);
        return oldFilm;
    }
}

