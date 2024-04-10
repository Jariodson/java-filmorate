package ru.yandex.practicum.filmorate.storage.mapper;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmMapper implements RowMapper<Film> {
    final JdbcTemplate jdbcTemplate;
    final MpaMapper mpaMapper;
    final GenreMapper genreMapper;
    final DirectorMapper directorMapper;

    @Autowired
    public FilmMapper(JdbcTemplate jdbcTemplate, MpaMapper mpaMapper, GenreMapper genreMapper,
                      DirectorMapper directorMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaMapper = mpaMapper;
        this.genreMapper = genreMapper;
        this.directorMapper = directorMapper;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("released_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(findMpa(rs.getLong("mpa_id")))
                .genres(findGenres(rs.getLong("film_id")))
                .directors(findDirector(rs.getLong("film_id")))
                .build();
        return film;
    }

    public Mpa findMpa(Long ratingId) {
        final String mpaSql = "SELECT * " +
                "FROM mpa " +
                "WHERE mpa_id = ?";

        return jdbcTemplate.queryForObject(mpaSql, mpaMapper, ratingId);
    }

    protected List<Genre> findGenres(Long filmId) {
        final String genreSql = "SELECT genre.genre_id, genre.genre_name " +
                "FROM genre " +
                "LEFT JOIN genre_of_film AS fg ON genre.genre_id = fg.genre_id " +
                "WHERE film_id = ?";

        return jdbcTemplate.query(genreSql, genreMapper, filmId);
    }


    public List<Director> findDirector(Long id) {
        String sqlQuery =
                "SELECT * FROM director d " +
                        "JOIN director_of_film f ON f.director_id = d.director_id " +
                        "WHERE f.film_id = ?;";
        return jdbcTemplate.query(sqlQuery, directorMapper, id);
    }
}
