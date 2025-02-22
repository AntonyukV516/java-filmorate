package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Data
@Slf4j
public class FilmService {
    private final Map<Integer, Film> films = new HashMap<>();
    private static Integer globalId = 0;

    private static Integer getNextId() {
        return ++globalId;
    }

    public Map<Integer, Film> getFilms() {
        return films;
    }

    public Film addFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм : {}", film);
        return film;
    }

    public Film updateFilm(Film newFilm) {
        Optional<Film> optOldFilm = Optional.ofNullable(films.get(newFilm.getId()));
        if (optOldFilm.isPresent()) {
            Film oldFilm = optOldFilm.get();
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Старый фильм после обновления : {}", oldFilm);
            return oldFilm;
        } else throw new NullPointerException("Нельзя обновлять не созданный фильм");
    }
}
