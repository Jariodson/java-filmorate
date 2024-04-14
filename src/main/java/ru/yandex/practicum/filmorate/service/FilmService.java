package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.FilmParameter;
import ru.yandex.practicum.filmorate.model.enums.SortParam;

import java.util.Collection;
import java.util.Optional;

public interface FilmService {
    Collection<Film> getFilms();

    Film getFilmById(Long id);

    Collection<Film> getDirectorFilmsSorted(Long directorId, Optional<SortParam[]> orderBy);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film removeFilm(Long id);

    Film createLike(Long filmId, Long userId);

    Film removeLike(Long filmId, Long userId);

    Collection<Film> getCommonFilms(Long userId, Long friendId);

    Collection<Film> getMostPopularsFilms(Integer count, Optional<Long> genreId, Optional<Integer> year);

    Collection<Film> searchFilmByParameter(String lowerCase, FilmParameter lowerCase1);
}