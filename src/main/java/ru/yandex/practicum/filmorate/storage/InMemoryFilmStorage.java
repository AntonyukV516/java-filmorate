package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Data
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private static Integer globalId = 0;

    private static Integer getNextId() {
        return ++globalId;
    }

    @Override
    public Map<Integer, Film> getFilms() {
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм : {}", film);
        return film;
    }

    @Override
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
