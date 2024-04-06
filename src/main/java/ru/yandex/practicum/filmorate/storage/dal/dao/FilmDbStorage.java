package ru.yandex.practicum.filmorate.storage.dal.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
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
        List<Film> films = jdbcTemplate.query("SELECT f.*, m.mpa_name, f.mpa_id " +
                "FROM film AS f " +
                "JOIN mpa AS m ON m.mpa_id = f.mpa_id", this::makeFilm);
        return films;
    }

    @Override
    public Film getFilmById(Long id) {
        Film film = jdbcTemplate.queryForObject("SELECT f.*, mpa.mpa_name " +
                        "FROM film AS f " +
                        "LEFT JOIN mpa ON mpa.mpa_id = f.mpa_id " +
                        "WHERE film_id=?",
                this::makeFilm, id);
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
    }

    @Override
    public void deleteFilm(Long id) {
        jdbcTemplate.update("DELETE FROM film WHERE film_id = ?", id);
    }

    @Override
    public Collection<Film> getFavouriteFilms(int count) {
        String sql = "SELECT f.*, m.mpa_name, COUNT(l.user_id) AS like_count " +
                "FROM film AS f " +
                "JOIN FILM_LIKE AS l ON l.film_id = f.film_id " +
                "JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "GROUP BY f.film_id, m.mpa_name " +
                "ORDER BY like_count DESC " +
                "LIMIT ? ";
        List<Film> films = jdbcTemplate.query(sql, this::makeFilm, count);
        return films;
    }

    private Collection<Film> getFilmsByCount(int count) {
        return jdbcTemplate.query("SELECT f.*, mpa.mpa_name, mpa.mpa_id " +
                        "FROM film AS f " +
                        "LEFT JOIN mpa ON mpa.mpa_id = f.mpa_id " +
                        "LIMIT ?;",
                this::makeFilm, count);
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

        return film;
    }

    @Override
    public Collection<Film> getFilmsByDirectorAndSort(Long directorId, String[] orderBy) {
        try {
            // Construct the base SQL query
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT f.* FROM film f ");
            sqlBuilder.append("JOIN director_of_film df ON f.film_id = df.film_id ");
            sqlBuilder.append("WHERE df.director_id = ? ");

            // Check if orderBy array is provided and construct ORDER BY clause
            if (orderBy != null && orderBy.length > 0) {
                sqlBuilder.append("ORDER BY ");
                for (int i = 0; i < orderBy.length; i++) {
                    sqlBuilder.append(orderBy[i]);
                    if (i < orderBy.length - 1) {
                        sqlBuilder.append(", ");
                    }
                }
            }

            // Execute the query
            String sql = sqlBuilder.toString();
            Collection<Film> films = jdbcTemplate.query(sql, this::makeFilm, directorId);
            return films;
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Failed to retrieve films by director and sort", e);
        }
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }
}
