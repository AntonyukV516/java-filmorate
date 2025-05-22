package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class})
class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    void getGenres() {
        Collection<Genre> genres = genreDbStorage.getGenres();
        assertThat(genres.size()).isEqualTo(6);
    }

    @Test
    void getGenreById() {
        Genre genre = genreDbStorage.getGenreById(1);
        assertThat(genre)
                .hasFieldOrPropertyWithValue("id", 1);
    }
}