package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.*;
import ru.yandex.practicum.filmorate.storage.dal.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dal.LikeDal;


import java.util.Collection;

@Slf4j
@Service
@Transactional
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final LikeDal likeStorage;
    private final UserService userService;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final DirectorService directorService;

    @Autowired
    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage, LikeDal likeStorage, UserService userService, MpaService mpaService, GenreService genreService, DirectorService directorService) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.userService = userService;
        this.mpaService = mpaService;
        this.genreService = genreService;
        this.directorService = directorService;
    }

    @Override
    public Collection<Film> getFilms() {
        Collection<Film> films = filmStorage.getAllFilms();
        for (Film film : films) {
            long id = film.getId();
            film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
            film.setGenres(genreService.getFilmsGenre(id));
            film.setDirectors(directorService.getFilmsDirector(id));
        }
        return films;
    }

    public Film getFilmById(Long id) {
        checkFilmInDb(id);
        Film film = filmStorage.getFilmById(id);
        film.setGenres(genreService.getFilmsGenre(id));
        film.setDirectors(directorService.getFilmsDirector(id));
        return film;
    }

    @Override
    public Collection<Film> getFavoriteFilms(int count) {
        Collection<Film> films = filmStorage.getFavouriteFilms(count);
        for (Film film : films) {
            long id = film.getId();
            film.setGenres(genreService.getFilmsGenre(id));
            film.setDirectors(directorService.getFilmsDirector(id));
        }
        return films;
    }

    public Collection<Film> getDirectorFilmsSorted(Long directorId, String[] orderBy) {
        directorService.getDirectorById(directorId);
        Collection<Film> films = filmStorage.getFilmsByDirectorAndSort(directorId, orderBy);
        for (Film film : films) {
            long id = film.getId();
            film.setGenres(genreService.getFilmsGenre(id));
            film.setDirectors(directorService.getFilmsDirector(id));
        }
        return films;
    }

    @Override
    public Film createFilm(Film film) {
        checkMpa(film.getMpa().getId());

        filmStorage.addNewFilm(film);
        long filmId = film.getId();
        if (film.getGenres() != null) {
            genreService.updateFilmsGenre(filmId, film.getGenres());
        }
        if (film.getDirectors() != null) {
            directorService.updateFilmDirectors(filmId, film.getDirectors());
        }
        return getFilmById(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilmInDb(film.getId());
        filmStorage.updateFilm(film);
        genreService.updateFilmsGenre(film.getId(), film.getGenres());
        directorService.updateFilmDirectors(film.getId(), film.getDirectors());
        return getFilmById(film.getId());
    }

    @Override
    public Film removeFilm(Long id) {
        checkFilmInDb(id);
        Film film = filmStorage.getFilmById(id);
        filmStorage.deleteFilm(id);
        return film;
    }

    @Override
    public Film createLike(Long filmId, Long userId) {
        checkFilmInDb(filmId);
        checkUserInDb(userId);
        likeStorage.addLike(filmId, userId);
        return getFilmById(filmId);
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        checkFilmInDb(filmId);
        checkUserInDb(userId);
        likeStorage.removeLike(filmId, userId);

        return getFilmById(filmId);
    }

    @Override
    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        userService.getUserById(userId);
        userService.getUserById(friendId);
        var films = filmStorage.getCommonFilms(userId, friendId);
        for (Film film : films) {
            long id = film.getId();
            film.setGenres(genreService.getFilmsGenre(id));
            film.setDirectors(directorService.getFilmsDirector(id));
        }
        return films;
    }

    private void checkMpa(Long id) {
        try {
            mpaService.getMpaById(id);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Рейтин с ID: " + id + " не найден!");
        }
    }

    private void checkFilmInDb(Long id) {
        try {
            filmStorage.getFilmById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Фильм с ID: " + id + " не найден!");
        }
    }

    private void checkUserInDb(Long id) {
        try {
            userService.getUserById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Фильм с ID: " + id + " не найден!");
        }
    }
}
