package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.UserFeed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.FilmParameter;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.model.enums.SortParam;
import ru.yandex.practicum.filmorate.service.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserFeedStorage;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;


@Slf4j
@Service
@Transactional
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserFeedStorage feedStorage;
    private final UserService userService;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final DirectorService directorService;

    @Autowired
    public FilmServiceImpl(@Qualifier("dbFilmStorage") FilmStorage filmStorage, LikeStorage likeStorage, UserFeedStorage userFeedStorage, UserService userService, MpaService mpaService, GenreService genreService, DirectorService directorService) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.feedStorage = userFeedStorage;
        this.userService = userService;
        this.mpaService = mpaService;
        this.genreService = genreService;
        this.directorService = directorService;
    }

    @Override
    public Collection<Film> getFilms() {
        Collection<Film> films = filmStorage.getAllFilms();
        for (Film film : films) {
            buildFilm(film);
        }
        return films;
    }

    public Film getFilmById(Long id) {
        validateFilmId(id);
        Film film = filmStorage.getFilmById(id);
        buildFilm(film);
        return film;
    }

    public Collection<Film> getDirectorFilmsSorted(Long directorId, Optional<SortParam[]> orderBy) {
        directorService.validateDirectorId(directorId);
        Collection<Film> films = filmStorage.getFilmsByDirectorAndSort(directorId, orderBy);
        for (Film film : films) {
            buildFilm(film);
        }
        return films;
    }

    @Override
    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        userService.validateUserId(userId);
        userService.validateUserId(friendId);

        Collection<Film> films = filmStorage.getCommonFilms(userId, friendId);
        for (Film film : films) {
            buildFilm(film);
        }
        return films;
    }

    @Override
    public Collection<Film> getMostPopularsFilms(Integer count, Optional<Long> genreId, Optional<Integer> year) {
        Collection<Film> films = filmStorage.getMostPopularsFilms(count, genreId, year);
        for (Film film : films) {
            buildFilm(film);
        }
        return films;
    }

    @Override
    public Collection<Film> searchFilmByParameter(String query, FilmParameter[] sortTypes) {
        Collection<Film> films = filmStorage.searchFilmByParameter(query, sortTypes);
        for (Film film : films) {
            buildFilm(film);
        }
        return films;
    }

    @Override
    public Film createFilm(Film film) {
        mpaService.validateMpaId(film.getMpa().getId());

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
        validateFilmId(film.getId());
        filmStorage.updateFilm(film);
        genreService.updateFilmsGenre(film.getId(), film.getGenres());
        directorService.updateFilmDirectors(film.getId(), film.getDirectors());
        return getFilmById(film.getId());
    }

    @Override
    public Film removeFilm(Long id) {
        validateFilmId(id);
        Film film = filmStorage.getFilmById(id);
        filmStorage.deleteFilm(id);
        return film;
    }

    @Override
    public Film createLike(Long filmId, Long userId) {
        validateFilmId(filmId);
        userService.validateUserId(userId);
        likeStorage.addLike(filmId, userId);
        feedStorage.addUserFeed(new UserFeed(0L,
                userId, filmId, Instant.now(),
                EventType.LIKE, Operation.ADD
        ));
        return getFilmById(filmId);
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        validateFilmId(filmId);
        userService.validateUserId(userId);
        likeStorage.removeLike(filmId, userId);
        feedStorage.addUserFeed(new UserFeed(0L,
                userId, filmId, Instant.now(),
                EventType.LIKE, Operation.REMOVE
        ));
        return getFilmById(filmId);
    }

    @Override
    public void validateFilmId(Long id) {
        try {
            filmStorage.getFilmById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с ID: " + id + " не найден!");
        }
    }

    private void buildFilm(Film film) {
        long id = film.getId();
        film.setGenres(genreService.getFilmsGenre(id));
        film.setDirectors(directorService.getFilmsDirector(id));
        film.setLikes(new HashSet<>(likeStorage.getLikes(id)));
    }
}