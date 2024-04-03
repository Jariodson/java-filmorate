package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film getFilmById(Long id);

    void addNewFilm(Film film);

    void updateFilm(Film film);

    void deleteFilm(Long id);

    Collection<Film> getFavouriteFilms(int count);

    Film addLike(Long filmId, Long userId);

    Film removeLike(Long filmId, Long userId);
}
