package ru.yandex.practicum.filmorate.storage.dal.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dal.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> getAllFilms() {
        List<Film> films = jdbcTemplate.query("SELECT f.*, m.mpa_name, f.mpa_id, " +
                "FROM film AS f " +
                "JOIN mpa AS m ON m.mpa_id = f.mpa_id", this::makeFilm);

        String sql = "SELECT g.genre_id, g.genre_name FROM genre_of_film AS gf " +
                "JOIN genre AS g ON g.genre_id = gf.genre_id " +
                "WHERE gf.film_id = ? " +
                "ORDER BY g.genre_id";
        for (Film film : films) {
            List<Genre> genres = jdbcTemplate.query(sql, this::makeGenre, film.getId());
            film.setGenres(new HashSet<>(genres));
        }
        return films;
    }

    @Override
    public Film getFilmById(Long id) {
        Film film = jdbcTemplate.queryForObject("SELECT f.*, mpa.mpa_name, mpa.mpa_id " +
                        "FROM film AS f " +
                        "LEFT JOIN mpa ON mpa.mpa_id = f.mpa_id " +
                        "WHERE film_id=?",
                this::makeFilm, id);
        String sql = "SELECT g.genre_id, g.genre_name FROM genre AS g " +
                "JOIN genre_of_film AS gf ON g.genre_id = gf.genre_id " +
                "WHERE gf.film_id = ? " +
                "ORDER BY g.genre_id";
        if (film != null) {
            List<Genre> genres = jdbcTemplate.query(sql, this::makeGenre, film.getId());
            film.setGenres(new HashSet<>(genres));
        }
        return film;
    }

    @Override
    public void addNewFilm(Film film) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id")
                .usingColumns("name", "description", "released_date", "duration", "mpa_id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("released_date", film.getReleaseDate());
        parameters.put("duration", film.getDuration());
        parameters.put("mpa_id", film.getMpa().getId());
        Long id = jdbcInsert.executeAndReturnKey(parameters).longValue();
        film.setId(id);
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO genre_of_film (film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genre.getId());
            }
        }
    }

    @Override
    public void updateFilm(Film film) {
        String sql = "UPDATE film SET name = ?, description = ?, released_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        jdbcTemplate.update("DELETE FROM genre_of_film WHERE film_id = ?", film.getId());
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update("INSERT INTO genre_of_film (film_id, genre_id) VALUES (?, ?)",
                    film.getId(), genre.getId());
        }
    }

    @Override
    public void deleteFilm(Long id) {
        jdbcTemplate.update("DELETE FROM \"like\" WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM genre_of_film WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM film WHERE film_id = ?", id);
    }

    @Override
    public Collection<Film> getFavouriteFilms(int count) {
        String sql = "SELECT f.*, m.mpa_name, COUNT(l.user_id) AS like_count " +
                "FROM film AS f " +
                "JOIN \"like\" AS l ON l.film_id = f.film_id " +
                "JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "GROUP BY f.film_id, m.mpa_name " +
                "ORDER BY like_count DESC " +
                "LIMIT ? ";
        Collection<Film> films = jdbcTemplate.query(sql, this::makeFilm, count);

        if (films.isEmpty()) {
            films = getFilmsByCount(count);
        }
        String sqlGenre = "SELECT g.genre_id, g.genre_name FROM genre_of_film AS gf " +
                "JOIN genre AS g ON g.genre_id = gf.genre_id " +
                "WHERE gf.film_id = ? " +
                "ORDER BY g.genre_id";
        for (Film film : films) {
            List<Genre> genres = jdbcTemplate.query(sqlGenre, this::makeGenre, film.getId());
            film.setGenres(new HashSet<>(genres));
        }
        return films;
    }

    private Collection<Film> getFilmsByCount(int count) {
        return jdbcTemplate.query("SELECT f.*, mpa.mpa_name, mpa.mpa_id " +
                        "FROM film AS f " +
                        "LEFT JOIN mpa ON mpa.mpa_id = f.mpa_id " +
                        "LIMIT ?;",
                this::makeFilm, count);
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO \"like\" (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
        return getFilmById(filmId);
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM \"like\" WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
        return getFilmById(filmId);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("released_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getLong("mpa_id"), rs.getString("mpa_name")))
                .build();

        String sql = "SELECT user_id FROM \"like\" WHERE film_id = ? ";
        Set<Long> likes = jdbcTemplate.query(sql, rs1 -> {
            Set<Long> list = new HashSet<>();
            while (rs1.next()) {
                list.add(rs1.getLong("user_id"));
            }
            return list;
        }, film.getId());
        if (likes != null) {
            film.setLikes(likes);
        }

        return film;
    }

    @Override
    public Collection<Film> getFilmsByDirectorAndSort(Long directorId, String[] orderBy) {
        return null;
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }
}
