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
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.dal.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final MpaService mpaService;
    private final GenreService genreService;

    @Autowired
    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserService userService,
                           MpaService mpaService, GenreService genreService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    @Override
    public Collection<Film> getFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film getFilmById(Long id) {
        checkFilmInDb(id);
        return filmStorage.getFilmById(id);
    }

    @Override
    public Collection<Film> getFavouriteFilms(int count) {
        return filmStorage.getFavouriteFilms(count);
    }

    @Override
    public Film addFilm(Film film) {
        checkFilmCriteria(film);
        checkMpa(film.getMpa().getId());
        checkGenre(film.getGenres());
        filmStorage.addNewFilm(film);
        film.getMpa().setName(mpaService.getMpaNameById(film.getMpa().getId()));
        for (Genre genre : film.getGenres()) {
            genre.setName(genreService.getGenreNameById(genre.getId()));
        }
        return film;
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        checkFilmInDb(filmId);
        userService.findUserById(userId);
        return filmStorage.addLike(filmId, userId);
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilmInDb(film.getId());
        checkFilmCriteria(film);
        checkMpa(film.getMpa().getId());
        checkGenre(film.getGenres());
        filmStorage.updateFilm(film);
        return filmStorage.getFilmById(film.getId());
    }

    @Override
    public Film deleteFilm(Film film) {
        checkFilmInDb(film.getId());
        checkFilmCriteria(film);
        checkMpa(film.getMpa().getId());
        checkGenre(film.getGenres());
        filmStorage.deleteFilm(film);
        return filmStorage.getFilmById(film.getId());
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        checkFilmInDb(filmId);
        userService.findUserById(userId);
        return filmStorage.removeLike(filmId, userId);
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

    private void checkGenre(Set<Genre> genres) {
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
