package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {
    Collection<Film> getFilms();

    Film getFilmById(Long id);

    Collection<Film> getDirectorFilmsSorted(Long directorId, String[] orderBy);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film removeFilm(Long id);

    Film createLike(Long filmId, Long userId);

    Film removeLike(Long filmId, Long userId);

    Collection<Film> getCommonFilms(Long userId, Long friendId);

    Collection<Film> getMostPopularsFilms(Integer count, Long genreId, Integer year);

    List<Film> searchFilmByParameter(String lowerCase, String lowerCase1);
}