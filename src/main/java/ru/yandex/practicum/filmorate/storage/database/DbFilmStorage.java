package ru.yandex.practicum.filmorate.storage.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.enums.FilmParameter;
import ru.yandex.practicum.filmorate.model.enums.SortParam;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Repository
@Qualifier("filmDbStorage")
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;


    public DbFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return jdbcTemplate.query("SELECT f.*, m.mpa_name, f.mpa_id " +
                "FROM film AS f " +
                "JOIN mpa AS m ON m.mpa_id = f.mpa_id", this::makeFilm);
    }

    @Override
    public Film getFilmById(Long id) {
        return jdbcTemplate.queryForObject("SELECT f.*, mpa.mpa_name " +
                        "FROM film AS f " +
                        "LEFT JOIN mpa ON mpa.mpa_id = f.mpa_id " +
                        "WHERE film_id=?",
                this::makeFilm, id);
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
    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        String sql = "SELECT DISTINCT f.*, m.mpa_name FROM film f " +
                "INNER JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "INNER JOIN (SELECT film_id FROM FILM_LIKE WHERE user_id = ?) AS u1_likes " +
                "ON f.film_id = u1_likes.film_id " +
                "INNER JOIN (SELECT film_id FROM FILM_LIKE WHERE user_id = ?) AS u2_likes " +
                "ON f.film_id = u2_likes.film_id";
        return jdbcTemplate.query(sql, this::makeFilm, userId, friendId);
    }

    @Override
    public Collection<Film> getFilmsByDirectorAndSort(Long directorId, Optional<SortParam[]> orderBy) {

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT f.*, m.mpa_name, COUNT(l.user_id) AS likes " +
                "FROM film AS f " +
                "LEFT JOIN FILM_LIKE AS l ON l.film_id = f.film_id " +
                "JOIN director_of_film df ON f.film_id = df.film_id " +
                "JOIN mpa AS m ON m.mpa_id = f.mpa_id ");
        sqlBuilder.append("WHERE df.director_id = ? ");
        sqlBuilder.append("GROUP BY f.film_id, m.mpa_name ");
        if (orderBy.isPresent()) {
            sqlBuilder.append("ORDER BY ");
            for (SortParam param : orderBy.get()) {
                switch (param) {
                    case likes:
                        sqlBuilder.append(" likes ");
                        break;
                    case year:
                        sqlBuilder.append(" f.released_date ");
                        break;
                }
                sqlBuilder.append(", ");
            }
            sqlBuilder.delete(sqlBuilder.length() - 2, sqlBuilder.length());
        }
        try {
            String sql = sqlBuilder.toString();
            return jdbcTemplate.query(sql, this::makeFilm, directorId);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public Collection<Film> getMostPopularsFilms(Integer count, Optional<Long> genreId, Optional<Integer> year) {

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT f.*, m.mpa_name, COUNT(l.user_id) AS like_count ")
                .append("FROM film AS f ")
                .append("LEFT JOIN film_like AS l ON l.film_id = f.film_id ")
                .append("LEFT JOIN mpa AS m ON m.mpa_id = f.mpa_id ");

        List<Object> params = new ArrayList<>();
        sqlBuilder.append("WHERE 1=1 ");

        if (genreId.isPresent()) {
            sqlBuilder.append("AND EXISTS (SELECT g.genre_id FROM genre_of_film g WHERE g.film_id = f.film_id and g.genre_id = ?) ");
            params.add(genreId.get());
        }

        if (year.isPresent()) {
            sqlBuilder.append("AND EXTRACT(year FROM f.released_date) = ? ");
            params.add(year.get());
        }

        sqlBuilder.append("GROUP BY f.film_id, m.mpa_name ")
                .append("ORDER BY like_count DESC ")
                .append("LIMIT ?");

        params.add(count);
        try {
            String sql = sqlBuilder.toString();
            return jdbcTemplate.query(sql, this::makeFilm, params.toArray());
        } catch (DataAccessException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public Collection<Film> searchFilmByParameter(String query, FilmParameter[] sortTypes) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT f.* , m.mpa_name ")
                .append("FROM film AS f ")
                .append("LEFT JOIN film_like AS l ON l.film_id = f.film_id ")
                .append("LEFT JOIN mpa AS m ON m.mpa_id = f.mpa_id ")
                .append("LEFT JOIN director_of_film AS fd ON f.film_id=fd.film_id ")
                .append("LEFT JOIN director AS d ON d.director_id=fd.director_id ");
        List<Object> params = new ArrayList<>();

        sqlBuilder.append("WHERE 1=0 ");
        for (FilmParameter type : sortTypes) {
            switch (type) {
                case director:
                    sqlBuilder.append(" OR d.director_name LIKE ? ");
                    params.add('%' + query + '%');
                    break;
                case title:
                    sqlBuilder.append(" OR LOWER(f.name) LIKE ? ");
                    params.add('%' + query + '%');
                    break;
            }
        }
        sqlBuilder.append("GROUP BY f.film_id ")
                .append("ORDER BY count(l.user_id) DESC");
        try {
            String sql = sqlBuilder.toString();
            return jdbcTemplate.query(sql, this::makeFilm, params.toArray());
        } catch (DataAccessException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {

        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("released_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getLong("mpa_id"), rs.getString("mpa_name")))
                .build();
    }

}