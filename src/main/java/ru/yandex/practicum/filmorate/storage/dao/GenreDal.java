package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreDal {
    Collection<Genre> getGenres();

    Genre getGenreById(Long id);


    Collection<Genre> getFilmGenre(Long filmId);

    void updateFilmsGenre(Long id, Collection<Genre> genres);
}
