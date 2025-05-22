package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaDbStorage.class})
class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    void getMpa() {
        Collection<Mpa> allMpa = mpaDbStorage.getMpa();
        assertThat(allMpa.size()).isEqualTo(5);
    }

    @Test
    void getMpaById() {
        Mpa mpa = mpaDbStorage.getMpaById(1);
        assertThat(mpa)
                .hasFieldOrPropertyWithValue("id", 1);
    }
}