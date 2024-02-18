package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    FilmStorage filmStorage = new InMemoryFilmStorage();

    public Collection<Film> getFilms() {
        log.info("Получен запрос GET на получение списка всех фильмов");
        log.info("Размер списка фильмов: {}", filmStorage.getAllFilms().size());
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Long id) {
        log.info("Получен запрос GET на получение фильма по ID: {}", id);
        if (filmStorage.getFilmById(id) != null) {
            return filmStorage.getFilmById(id);
        }
        throw new IllegalArgumentException("Фильма с ID " + id + "не существует!");
    }

    public Collection<Film> getFavouriteFilms(int count) {
        log.info("Получен запрос GET на получение самых популярных фильмов!");
        List<Film> films = filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparing(o -> {
                    if (o.getLikes() == null) {
                        return null;
                    } else {
                        return o.getLikes().size();
                    }
                }, Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(count)
                .collect(Collectors.toList());
        log.info("Вывод {} самых популярных фильмов. Id фильмов: {}", count,
                films.stream().map(Film::getId).collect(Collectors.toList()));
        return films;
    }

    public ResponseEntity<Film> addFilm(Film film) {
        log.info("Получен запрос POST на добавление фильма в список");
        filmStorage.addNewFilm(film);
        log.info("Фильм добавлен в список: {}. Размер списка: {}", film, filmStorage.getAllFilms().size());
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    public ResponseEntity<Film> addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        log.info("Получен запрос POST на добавление лайка. Id фильма: {}, Id пользователя: {}", filmId, userId);
        film.addLike(userId);
        log.info("Лайк успешно поставлен! Id фильма: {} ,Id пользователя: {}", filmId, userId);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    public ResponseEntity<Film> updateFilm(Film film) {
        log.info("Получен запрос PUT на обновления фильма в списке");
        filmStorage.updateFilm(film);
        log.info("Обновленный фильм: {} добавлен в список. Размер списка: {}", film, filmStorage.getAllFilms().size());
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    public ResponseEntity<Film> deleteFilm(Film film) {
        log.info("Получен запрос DELETE на удаление фильма");
        filmStorage.deleteFilm(film);
        log.info("Фильм удалён! {}", film);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    public ResponseEntity<Film> removeLike(Long filmId, Long userId) {
        log.info("Получен запрос DELETE на удаление лайка");
        Film film = filmStorage.getFilmById(filmId);
        if (film.getLikes() == null) {
            log.warn("Ошибка! У фильма с ID: {} еще нет лайков!", filmId);
            return new ResponseEntity<>(film, HttpStatus.NOT_FOUND);
        }
        film.removeLike(userId);
        log.info("Лайк пользователя {} успешно удалён!", userId);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }
}
