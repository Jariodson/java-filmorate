package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film getFilmById(Long id);

    void addNewFilm(Film film);

    void updateFilm(Film film);

    void deleteFilm(Long id);

    Collection<Film> getFilmsByDirectorAndSort(Long directorId, String[] orderBy);

    Collection<Film> getCommonFilms(Long userId, Long friendId);

    Collection<Film> getMostPopularsFilms(Integer count, Long genreId, Integer year);

    List<Film> searchFilmByParameter(String query, String filmSearchParameter);
}