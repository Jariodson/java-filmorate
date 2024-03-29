package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreDal {
    Collection<Genre> getGenres();

    Genre getGenreById(Long id);

    String getGenreNameById(Long id);
}
