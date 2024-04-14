package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.FilmParameter;
import ru.yandex.practicum.filmorate.model.enums.SortParam;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film getFilmById(Long id);

    void addNewFilm(Film film);

    void updateFilm(Film film);

    void deleteFilm(Long id);

    Collection<Film> getFilmsByDirectorAndSort(Long directorId, Optional<SortParam[]> orderBy);

    Collection<Film> getCommonFilms(Long userId, Long friendId);

    Collection<Film> getMostPopularsFilms(Integer count, Optional<Long> genreId, Optional<Integer> year);

    Collection<Film> searchFilmByParameter(String query, FilmParameter[] sortTypes);

}