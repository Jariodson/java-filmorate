package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {
    Collection<Film> getFilms();

    Film getFilmById(Long id);

    Collection<Film> getFavoriteFilms(int count);

    Collection<Film> getDirectorFilmsSorted(Long directorId, String[] orderBy);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film removeFilm(Long id);

    Film createLike(Long filmId, Long userId);

    Film removeLike(Long filmId, Long userId);
}
