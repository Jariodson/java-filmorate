package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.*;
import ru.yandex.practicum.filmorate.storage.dal.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dal.LikeDal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage, LikeDal likeStorage, UserService userService,
                           MpaService mpaService, GenreService genreService, DirectorService directorService) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.userService = userService;
        this.mpaService = mpaService;
        this.genreService = genreService;
        this.directorService = directorService;
    }



    @Override
    public Collection<Film> getFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Long id) {
        checkFilmInDb(id);
        var film = filmStorage.getFilmById(id);
        film.getMpa().setName(mpaService.getMpaNameById(film.getMpa().getId()));
        film.setGenres( genreService.getFilmsGenre(id));
        film.setDirectors(directorService.getFilmsDirector(id));
        return film;
    }

    @Override
    public Collection<Film> getFavouriteFilms(int count) {
        return filmStorage.getFavouriteFilms(count);
    }

    public Collection<Film> getFilmsByDirectorAndSort(Long directorId, String[] orderBy) {
        return filmStorage.getFilmsByDirectorAndSort(directorId, orderBy);
    }

    @Override
    public Film addFilm(Film film) {
        checkFilmCriteria(film);
        checkMpa(film.getMpa().getId());
        checkGenre(film.getGenres());
        filmStorage.addNewFilm(film);
        long filmId = film.getId();
            genreService.addFilmsGenre(filmId,film.getGenres());

        if (film.getDirectors()!=null){
            directorService.addFilmsDirector(filmId, film.getDirectors());
        }

        return getFilmById(film.getId());
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        checkFilmInDb(filmId);
        userService.findUserById(userId);

        likeStorage.addLike(filmId, userId);

        return  getFilmById(filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilmInDb(film.getId());
        checkFilmCriteria(film);
        checkMpa(film.getMpa().getId());
        checkGenre(film.getGenres());
        filmStorage.updateFilm(film);
        return getFilmById(film.getId());
    }

    @Override
    public Film deleteFilm(Long id) {
        checkFilmInDb(id);
        Film film = filmStorage.getFilmById(id);
        filmStorage.deleteFilm(id);
        return film;
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        checkFilmInDb(filmId);
        userService.findUserById(userId);
        likeStorage.removeLike(filmId, userId);

        return  getFilmById(filmId);
    }

    private void checkFilmCriteria(Film film) {
        if (film.getReleaseDate() != null) {
            LocalDate filmBirthday = LocalDate.of(1895, 12, 28);
            if (film.getReleaseDate().isBefore(filmBirthday)) {
                throw new ValidationException("Слшиком ранняя дата релиза! " + film.getReleaseDate());
            }
        }
    }

    private void checkMpa(Long id) {
        try {
            mpaService.getMpaById(id);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Рейтин с ID: " + id + " не найден!");
        }
    }

    private void checkGenre(Collection<Genre> genres) {
        for (Genre genre : genres) {
            try {
                genreService.getGenreById(genre.getId());
            } catch (IllegalArgumentException e) {
                throw new NotFoundException("Жанр с ID: " + genre.getId() + " не найден!");
            }
        }
    }

    private void checkFilmInDb(Long id) {
        try {
            filmStorage.getFilmById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Фильм с ID: " + id + " не найден!");
        }
    }
}
