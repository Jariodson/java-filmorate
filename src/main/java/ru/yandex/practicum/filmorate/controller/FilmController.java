package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@ResponseStatus(HttpStatus.NOT_FOUND)
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getFilms() {
        log.info("Получен запрос GET на получение списка всех фильмов");
        Collection<Film> films = filmService.getFilms();
        log.info("Вывод фильмов. Размер списка фильмов: {}", films.size());
        return films;
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film getFilmById(@PathVariable Optional<Long> id) {
        log.info("Получен запрос GET на получение фильма по ID: {}", id);
        if (id.isPresent()) {
            Film film = filmService.getFilmById(id.get());
            log.info("Вывод фильма с Id: {}", id);
            return film;
        }
        throw new IllegalArgumentException("Введен неверный индефикатор! Id: " + id);
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос POST на добавление фильма в список");
        Film newFilm = filmService.addFilm(film);
        log.info("Фильм добавлен в список: {}. Размер списка: {}", film, filmService.getFilms().size());
        return new ResponseEntity<>(newFilm, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос PUT на обновления фильма в списке");
        Film newFilm = filmService.updateFilm(film);
        log.info("Обновленный фильм: {} добавлен в список. Размер списка: {}", film, filmService.getFilms().size());
        return new ResponseEntity<>(newFilm, HttpStatus.OK);
    }

    @DeleteMapping("{filmId}")
    public ResponseEntity<Film> deleteFilm(@PathVariable(value = "filmId") Optional<Long> id) {
        if (id.isPresent()) {
            log.info("Получен запрос DELETE на удаление фильма");
            Film film = filmService.deleteFilm(id.get());
            log.info("Фильм c ID: {} удалён!", id);
            return new ResponseEntity<>(film, HttpStatus.OK);
        }
        throw new IllegalArgumentException("Введён неверный индефикатор! Id: " + id);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable(value = "id") Optional<Long> filmId,
                                        @PathVariable Optional<Long> userId) {
        log.info("Получен запрос PUT на добавление лайка. Id фильма: {}, Id пользователя: {}", filmId, userId);
        if (filmId.isPresent() && userId.isPresent()) {
            Film film = filmService.addLike(filmId.get(), userId.get());
            log.info("Лайк успешно поставлен! Id фильма: {} ,Id пользователя: {}", filmId, userId);
            return new ResponseEntity<>(film, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("Введён неверный индефикатор! Id: " + filmId + " или Id: " + userId);
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> removeLike(@PathVariable(value = "id") Optional<Long> filmId,
                                           @PathVariable Optional<Long> userId) {
        log.info("Получен запрос DELETE на удаление лайка");
        if (filmId.isPresent() && userId.isPresent()) {
            Film film = filmService.removeLike(filmId.get(), userId.get());
            log.info("Лайк пользователя {} успешно удалён!", userId);
            return new ResponseEntity<>(film, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("Введён неверный индефикатор! Id: " + filmId + " или Id: " + userId);
        }
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getFavouriteFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос GET на получение самых популярных фильмов!");
        Collection<Film> films = filmService.getFavouriteFilms(count);
        log.info("Вывод {} популярных фильмов", count);
        return films;
    }

    @GetMapping("/films/director/{directorId}")
    public Collection<Film> getFilmsByDirector(@PathVariable Long directorId,
                                               @RequestParam(required = false) String[] sortBy) {
        //@todo это тоже недопилено
        log.info("Получен запрос GET на получение фильмов по режисёру!");
        Collection<Film> films = filmService.getFilmsByDirectorAndSort(directorId, sortBy);
        return films;
    }

}
