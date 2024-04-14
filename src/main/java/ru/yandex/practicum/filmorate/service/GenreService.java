package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;


public interface GenreService {
    Collection<Genre> getGenres();

    Genre getGenreById(Long id);

    Collection<Genre> getFilmsGenre(Long id);

    void updateFilmsGenre(Long id, Collection<Genre> genres);

    void validateGenreId(Collection<Genre> genres);
}
