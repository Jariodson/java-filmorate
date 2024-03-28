package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.dal.FilmStorage;

import java.util.Collection;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public Collection<Film> getFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    @Override
    public Collection<Film> getFavouriteFilms(int count) {
        return filmStorage.getFavouriteFilms(count);
    }

    @Override
    public Film addFilm(Film film) {
        filmStorage.addNewFilm(film);
        return film;
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        return filmStorage.addLike(filmId, userId);
    }

    @Override
    public Film updateFilm(Film film) {
        filmStorage.updateFilm(film);
        return film;
    }

    @Override
    public Film deleteFilm(Film film) {
        filmStorage.deleteFilm(film);
        return film;
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        return filmStorage.removeLike(filmId, userId);
    }
}
