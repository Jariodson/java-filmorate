package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;


public interface GenreService {
    Collection<Genre> getGenres();

    Genre getGenreById(Long id);

    String getGenreNameById(Long id);

    Collection<Genre> getFilmsGenre(Long id);

    void addFilmsGenre(Long userId, Collection<Genre> genres);
}
