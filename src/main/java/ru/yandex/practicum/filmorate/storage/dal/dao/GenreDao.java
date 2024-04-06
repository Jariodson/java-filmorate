package ru.yandex.practicum.filmorate.storage.dal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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

    @Override
    public Collection<Genre> getFilmGenre(Long filmId) {
        String sql = "SELECT g.* FROM GENRE_OF_FILM f LEFT JOIN GENRE g ON g.GENRE_ID = f.GENRE_ID WHERE f.FILM_ID = ?";
        Collection<Genre> genres = jdbcTemplate.query(sql, this::makeGenre, filmId);
        return genres;
    }

    @Override
    public void addFilmsGenre(Long userId, Collection<Genre> genres) {
        if (genres != null) {
            for (Genre g : genres) {
                addFilmGenre(userId, g);
            }
        }
    }

    public void addFilmGenre(Long filmId, Genre genreId) {
        String sql = "INSERT INTO GENRE_OF_FILM (film_id, genre_id)" +
                "SELECT ?, ?" +
                "WHERE NOT EXISTS (" +
                "    SELECT 1 FROM GENRE_OF_FILM" +
                "    WHERE film_id = ? AND genre_id = ?" + ")";
        try {
            jdbcTemplate.update(sql, filmId, genreId.getId(), filmId, genreId.getId());
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при добавлении жанра");
        }
    }


    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }
}
