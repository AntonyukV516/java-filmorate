package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class})
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final JdbcTemplate jdbc;

    private Film film;
    private Film film2;

    @BeforeEach
    void setUp() {
        jdbc.update("DELETE FROM film");
        jdbc.update("ALTER TABLE film ALTER COLUMN id RESTART WITH 1");

        film = new Film();
        film.setName("filmName");
        film.setDuration(100);
        film.setDescription("description");
        film.setReleaseDate(LocalDate.now());
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        Genre genre = new Genre();
        Genre genre2 = new Genre();
        genre.setId(1);
        genre2.setId(2);
        film.setGenres(Set.of(genre, genre2));
        filmDbStorage.addFilm(film);

        film2 = new Film();
        film2.setName("filmName2");
        film2.setDuration(200);
        film2.setDescription("description2");
        film2.setReleaseDate(LocalDate.now());
        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        film2.setMpa(mpa2);
        filmDbStorage.addFilm(film2);
    }

    @Test
    void getFilms() {
        Collection<Film> allFilms = filmDbStorage.getFilms();
        assertThat(allFilms.size()).isEqualTo(2);

        List<String> names = allFilms.stream()
                .map(Film::getName)
                .toList();
        assertThat(names.contains("filmName")).isTrue();
    }

    @Test
    void getFilmById() {
        Film film1 = filmDbStorage.getFilmById(film.getId());
        assertThat(film1)
                .hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void updateFilm() {
        film.setName("Updated name");
        filmDbStorage.updateFilm(film);
        Collection<Film> allFilms = filmDbStorage.getFilms();
        assertThat(allFilms.size()).isEqualTo(2);

        List<String> names = allFilms.stream()
                .map(Film::getName)
                .toList();
        assertThat(names.contains("Updated name")).isTrue();
    }

    @Test
    void getGenres() {
        Collection<Genre> genres = filmDbStorage.getGenres();
        assertThat(genres.size()).isEqualTo(6);
    }

    @Test
    void getGenreById() {
        Genre genre = filmDbStorage.getGenreById(film.getId());
        assertThat(genre)
                .hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void getMpa() {
        Collection<Mpa> allMpa = filmDbStorage.getMpa();
        assertThat(allMpa.size()).isEqualTo(5);
    }

    @Test
    void getMpaById() {
        Mpa mpa = filmDbStorage.getMpaById(1);
        assertThat(mpa)
                .hasFieldOrPropertyWithValue("id", 1);
    }
}