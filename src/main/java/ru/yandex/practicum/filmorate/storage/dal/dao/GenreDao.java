package ru.yandex.practicum.filmorate.storage.dal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dal.GenreDal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class GenreDao implements GenreDal {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> getGenres() {
        String sql = "SELECT * FROM genre";
        return jdbcTemplate.query(sql, this::makeGenre);
    }

    @Override
    public Genre getGenreById(Long id) {
        String sql = "SELECT * FROM genre WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sql, this::makeGenre, id);
    }

    @Override
    public String getGenreNameById(Long id) {
        String sql = "SELECT genre_name FROM genre WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, id);
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }
}
