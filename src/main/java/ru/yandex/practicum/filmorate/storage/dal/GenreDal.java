package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreDal {
    Collection<Genre> getGenres();

    Genre getGenreById(Long id);

    String getGenreNameById(Long id);

    Collection<Genre> getFilmGenre(Long filmId);

    void addFilmsGenre(Long userId,Collection<Genre> genres);
}
