package ru.yandex.practicum.filmorate.storage.dal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dal.DirectorDal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;


@Repository
public class DirectorDao implements DirectorDal {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Director> getAllDirectors() {
        String sql = "SELECT * FROM director";
        return jdbcTemplate.query(sql, this::makeDirector);
    }

    @Override
    public Director getDirectorById(Long id) {
        String sql = "SELECT * FROM director WHERE director_id = ?";
        return jdbcTemplate.queryForObject(sql, this::makeDirector, id);
    }

    @Override
    public void addNewDirector(Director newDirector) {
        String sql = "INSERT INTO director (director_id, director_name) VALUES (?,?)";
        jdbcTemplate.update(sql, newDirector.getName(), newDirector.getId());
    }

    @Override
    public void updateDirector(Director director) {
        String sql = "UPDATE director SET director_name = ? WHERE director_id = :?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
    }

    private Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }
}

