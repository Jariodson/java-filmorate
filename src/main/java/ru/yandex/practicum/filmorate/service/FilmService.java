package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {
    Collection<Film> getFilms();

    Film getFilmById(Long id);

    Collection<Film> getFavouriteFilms(int count);

    Film addFilm(Film film);

    Film addLike(Long filmId, Long userId);

    Film updateFilm(Film film);

    Film deleteFilm(Film film);

    Film removeLike(Long filmId, Long userId);
}
