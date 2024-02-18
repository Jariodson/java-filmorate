package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Long id) {
        if (filmStorage.getFilmById(id) != null) {
            return filmStorage.getFilmById(id);
        }
        throw new IllegalArgumentException("Фильма с ID " + id + "не существует!");
    }

    public Collection<Film> getFavouriteFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparing(o -> {
                    if (o.getLikes() == null) {
                        return null;
                    } else {
                        return o.getLikes().size();
                    }
                }, Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film addFilm(Film film) {
        filmStorage.addNewFilm(film);
        return film;
    }

    public Film addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userService.findUserById(userId);
        film.addLike(user.getId());
        return film;
    }

    public Film updateFilm(Film film) {
        filmStorage.updateFilm(film);
        return film;
    }

    public Film deleteFilm(Film film) {
        filmStorage.deleteFilm(film);
        return film;
    }

    public Film removeLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film.getLikes() == null) {
            log.warn("Ошибка! У фильма с ID: {} еще нет лайков!", filmId);
            throw new IllegalArgumentException("Фильм с Id " + filmId + " не найден");
        }
        film.removeLike(userId);
        return film;
    }
}
