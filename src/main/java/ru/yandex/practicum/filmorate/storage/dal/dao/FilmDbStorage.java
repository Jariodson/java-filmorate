package ru.yandex.practicum.filmorate.storage.dal.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dal.DirectorDal;
import ru.yandex.practicum.filmorate.storage.dal.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dal.GenreDal;
import ru.yandex.practicum.filmorate.storage.dal.LikeDal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDal genreDal;
    private final LikeDal likeDal;
    private final DirectorDal directorDal;

    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         GenreDal genreDal,
                         LikeDal likeDal,
                         DirectorDal directorDal) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDal = genreDal;
        this.likeDal = likeDal;
        this.directorDal = directorDal;
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

    private Collection<Film> getFilmsByCount(int count) {
        return jdbcTemplate.query("SELECT f.*, mpa.mpa_name, mpa.mpa_id " +
                        "FROM film AS f " +
                        "LEFT JOIN mpa ON mpa.mpa_id = f.mpa_id " +
                        "LIMIT ?;",
                this::makeFilm, count);
    }


    @Override
    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        String sql = "SELECT DISTINCT f.*, m.mpa_name FROM film f " +
                "INNER JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "INNER JOIN (SELECT film_id FROM FILM_LIKE WHERE user_id = ?) AS u1_likes " +
                "ON f.film_id = u1_likes.film_id " +
                "INNER JOIN (SELECT film_id FROM FILM_LIKE WHERE user_id = ?) AS u2_likes " +
                "ON f.film_id = u2_likes.film_id";
        String sqlGenre = "SELECT g.genre_id, g.genre_name FROM genre_of_film AS gf " +
                "JOIN genre AS g ON g.genre_id = gf.genre_id " +
                "WHERE gf.film_id = ? " +
                "ORDER BY g.genre_id";
        Collection<Film> films = jdbcTemplate.query(sql, this::makeFilm, userId, friendId);
        return films;
    }

    @Override
    public Collection<Film> getFilmsByDirectorAndSort(Long directorId, String[] orderBy) {
        try {
            // Construct the base SQL query
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT f.*, m.mpa_name, COUNT(l.user_id) AS likes " +
                    "FROM film AS f " +
                    "LEFT JOIN FILM_LIKE AS l ON l.film_id = f.film_id " +
                    "JOIN director_of_film df ON f.film_id = df.film_id " +
                    "JOIN mpa AS m ON m.mpa_id = f.mpa_id ");
            sqlBuilder.append("WHERE df.director_id = ? ");
            sqlBuilder.append("GROUP BY f.film_id, m.mpa_name ");
            // Check if orderBy array is provided and construct ORDER BY clause
            if (orderBy != null && orderBy.length > 0) {
                sqlBuilder.append("ORDER BY ");
                for (int i = 0; i < orderBy.length; i++) {
                    if (orderBy[i].equals("year")) {
                        orderBy[i] = " f.released_date ";
                    }
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

    @Override
    public Collection<Film> getMostPopularsFilms(Integer count, Long genreId, Integer year) {
        String sql = "SELECT f.*, m.mpa_name, COUNT(l.user_id) AS like_count " +
                "FROM film AS f " +
                "LEFT JOIN film_like AS l ON l.film_id = f.film_id " +
                "LEFT JOIN mpa AS m ON m.mpa_id = f.mpa_id ";
        Collection<Film> films;

        if (genreId != null && year != null) {
            sql += "WHERE " +
                    "EXISTS (SELECT g.genre_id FROM genre_of_film g WHERE g.film_id = f.film_id and g.genre_id = ?) " +
                    "AND EXTRACT(year FROM f.released_date) = ?" +
                    "GROUP BY f.film_id, m.mpa_name " +
                    "ORDER BY like_count DESC " +
                    "LIMIT ?";
            films = jdbcTemplate.query(sql, this::makeFilm, genreId, year, count);
        } else if (year != null) {
            sql += "WHERE EXTRACT(year FROM f.released_date) = ?" +
                    "GROUP BY f.film_id, m.mpa_name " +
                    "ORDER BY like_count DESC " +
                    "LIMIT ?";
            films = jdbcTemplate.query(sql, this::makeFilm, year, count);
        } else if (genreId != null) {
            sql += "WHERE " +
                    "EXISTS (SELECT g.genre_id FROM genre_of_film g WHERE g.film_id = f.film_id and g.genre_id = ?)" +
                    "GROUP BY f.film_id, m.mpa_name " +
                    "ORDER BY like_count DESC " +
                    "LIMIT ?";
            films = jdbcTemplate.query(sql, this::makeFilm, genreId, count);
        } else {
            sql += "GROUP BY f.film_id, m.mpa_name " +
                    "ORDER BY like_count DESC " +
                    "LIMIT ?";
            films = jdbcTemplate.query(sql, this::makeFilm, count);
        }

        return films.stream().peek(film -> {
                            film.setGenres(genreDal.getFilmGenre(film.getId()));
                            film.setLikes(new HashSet<>(likeDal.getLikes(film.getId())));
                            film.setDirectors(directorDal.getFilmsDirector(film.getId()));
                        }
                )
                .collect(Collectors.toList());
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
}