package ru.yandex.practicum.filmorate.storage.dal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dal.DirectorDal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Repository
public class DirectorDao implements DirectorDal {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Director> getAllDirectors() {
        String sql = "SELECT director_id, director_name FROM director";
        return jdbcTemplate.query(sql, this::makeDirector);
    }

    @Override
    public Director getDirectorById(Long id) {
        try {
            String sql = "SELECT * FROM director WHERE director_id = ?";
            return jdbcTemplate.queryForObject(sql, this::makeDirector, id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Фильм с ID: " + id + " не найден!");
        }
    }

    @Override
    public Director addNewDirector(Director newDirector) {
        try {
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("director")
                    .usingGeneratedKeyColumns("director_id");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("director_name", newDirector.getName());
            Number newDirectorId = jdbcInsert.executeAndReturnKey(parameters);

            newDirector.setId(newDirectorId.longValue());
            return newDirector;
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to add new director", e);
        }
    }

    @Override
    public Director updateDirector(Director director) {
        try {
            String sql = "UPDATE director SET director_name = ? WHERE director_id = ?";
            jdbcTemplate.update(sql, director.getName(), director.getId());
            return director;
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Director with ID: " + director.getId() + " not found!");
        }
    }

    @Override
    public void addFilmsDirector(Long userId, Collection<Director> directors) {
        if (directors != null) {
            for (Director g : directors) {
                addFilmsDirector(userId, g);
            }
        }
    }

    public void addFilmsDirector(Long filmId, Director genreId) {
        String sql = "INSERT INTO DIRECTOR_OF_FILM (film_id, director_id)" +
                "SELECT ?, ?" +
                "WHERE NOT EXISTS (" +
                "    SELECT 1 FROM DIRECTOR_OF_FILM" +
                "    WHERE film_id = ? AND director_id = ?" + ")";
        try {
            jdbcTemplate.update(sql, filmId, genreId.getId(), filmId, genreId.getId());
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при добавлении жанра");
        }
    }

    @Override
    public Collection<Director> getFilmsDirector(Long filmId) {
        try {
            String sql = "SELECT g.director_id, g.director_name FROM director_of_film f LEFT JOIN director g ON g.director_id = f.director_id WHERE f.FILM_ID = ?";
            return jdbcTemplate.query(sql, this::makeDirector, filmId);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Фильм с не найден!");
        }

    }

    @Override
    public void deleteDirector(Long id) {
        String sql = "DELETE FROM director WHERE director_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, id);
            if (rowsAffected == 0) {
                throw new IllegalArgumentException("Director with ID: " + id + " does not exist.");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Error occurred while deleting director with ID: " + id, e);
        }
    }

    private Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getLong("director_id"))
                .name(rs.getString("director_name"))
                .build();
    }
}

