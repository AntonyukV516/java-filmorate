package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .build();
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return new Mpa(
                resultSet.getInt("id"),
                resultSet.getString("name"));
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(
                resultSet.getInt("id"),
                resultSet.getString("name"));
    }

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO film (name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setDate(3, Date.valueOf(film.getReleaseDate()));
                ps.setInt(4, film.getDuration());
                ps.setInt(5, film.getMpa().getId());
                return ps;
            }, keyHolder);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Ошибка добавления фильма");
        }
        Integer generatedId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        film.setId(generatedId);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            addFilmGenres(film.getId(), film.getGenres());
        }

        log.info("Добавлен фильм : {}", film);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM film";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);
        if (films.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Integer, Mpa> mpaMap = getAllMpa();
        Map<Integer, Set<Genre>> genresByFilmId = getAllGenresByFilmId();
        Map<Integer, Set<Integer>> likesByFilmId = getAllLikesByFilmId();

        films.forEach(film -> {
            film.setMpa(mpaMap.get(film.getId()));
            film.setGenres(genresByFilmId.getOrDefault(film.getId(), new HashSet<>()));
            film.setLikedByUserIds(likesByFilmId.getOrDefault(film.getId(), new HashSet<>()));
        });

        return films;
    }

    @Override
    public Film getFilmById(Integer id) {
        String sql = "SELECT * FROM film WHERE id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id);

            film.setMpa(getMpa(id));
            film.setGenres(getFilmGenres(id));
            film.setLikedByUserIds(getLikesByFilmId(id));

            return film;

        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с id  " + id + " не найден");
        }
    }

    @Override
    public Film updateFilm(Film newFilm) {
        String updateSql = "UPDATE film SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, rating_id = ? WHERE id = ?";

        int updatedRows = jdbcTemplate.update(updateSql,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getMpa().getId(),
                newFilm.getId());
        if (updatedRows == 0) {
            throw new NullPointerException("Id не найден");
        }

        String deleteSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, newFilm.getId());

        Set<Genre> newGenres = newFilm.getGenres();
        if (newGenres != null && !newGenres.isEmpty()) {
            addFilmGenres(newFilm.getId(), newGenres);
        }

        String deleteLikesSql = "DELETE FROM film_likes WHERE film_id = ?";
        jdbcTemplate.update(deleteLikesSql, newFilm.getId());

        Set<Integer> newLikes = newFilm.getLikedByUserIds();
        if (newLikes != null && !newLikes.isEmpty()) {
            addFilmLikes(newFilm.getId(), newFilm.getLikedByUserIds());
        }
        return newFilm;
    }

    private void addFilmGenres(Integer filmId, Set<Genre> genres) {
        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

        List<Object[]> batchArgs = genres.stream()
                .map(genre -> new Object[]{filmId, genre.getId()})
                .toList();
        try {
            jdbcTemplate.batchUpdate(sql, batchArgs);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Ошибка добавления жанров");
        }
    }

    private Set<Integer> getLikesByFilmId(Integer id) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Integer.class, id));
    }

    private Set<Genre> getFilmGenres(Integer film_id) {
        String sql = "SELECT g.id, g.name FROM genre g " +
                "JOIN film_genre fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, this::mapRowToGenre, film_id));
    }

    private Mpa getMpa(Integer id) {
        String mpaSql = "SELECT rating_id FROM film WHERE id = ?";
        Integer mpaId = jdbcTemplate.queryForObject(mpaSql, Integer.class, id);
        String sqlQuery = "SELECT * FROM MPA WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, mpaId);
    }

    private void addFilmLikes(Integer filmId, Set<Integer> likes) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";

        List<Object[]> batchArgs = likes.stream()
                .map(like -> new Object[]{filmId, like})
                .toList();
        try {
            jdbcTemplate.batchUpdate(sql, batchArgs);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Ошибка добавления лайков");
        }
    }

    private Map<Integer, Mpa> getAllMpa() {
        String sql = "SELECT * FROM MPA";
        return jdbcTemplate.query(sql, rs -> {
            Map<Integer, Mpa> map = new HashMap<>();
            while (rs.next()) {
                map.put(rs.getInt("id"),
                        new Mpa(rs.getInt("id"), rs.getString("name")));
            }
            return map;
        });
    }

    private Map<Integer, Set<Genre>> getAllGenresByFilmId() {
        String sql = " SELECT fg.film_id, g.id, g.name FROM film_genre fg JOIN genre g ON fg.genre_id = g.id ";
        return jdbcTemplate.query(sql, rs -> {
            Map<Integer, Set<Genre>> map = new HashMap<>();
            while (rs.next()) {
                int filmId = rs.getInt("film_id");
                Genre genre = new Genre(rs.getInt("id"), rs.getString("name"));
                map.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
            }
            return map;
        });
    }

    private Map<Integer, Set<Integer>> getAllLikesByFilmId() {
        String sql = "SELECT film_id, user_id FROM film_likes";
        return jdbcTemplate.query(sql, rs -> {
            Map<Integer, Set<Integer>> map = new HashMap<>();
            while (rs.next()) {
                int filmId = rs.getInt("film_id");
                int userId = rs.getInt("user_id");
                map.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
            }
            return map;
        });
    }
}
