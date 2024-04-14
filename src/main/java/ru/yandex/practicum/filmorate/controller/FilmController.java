package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.FilmParameter;
import ru.yandex.practicum.filmorate.model.enums.SortParam;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/films")
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
    public Film getFilmById(@PathVariable Long id) {
        log.info("Получен запрос GET на получение фильма по ID: {}", id);
        Film film = filmService.getFilmById(id);
        log.info("Вывод фильма с Id: {}", id);
        return film;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос POST на добавление фильма в список");
        Film newFilm = filmService.createFilm(film);
        log.info("Фильм добавлен в список: {}. Размер списка: {}", film, filmService.getFilms().size());
        return newFilm;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос PUT на обновления фильма в списке");
        Film newFilm = filmService.updateFilm(film);
        log.info("Обновленный фильм: {} добавлен в список. Размер списка: {}", film, filmService.getFilms().size());
        return newFilm;
    }

    @DeleteMapping("{filmId}")
    @ResponseStatus(HttpStatus.OK)
    public Film deleteFilm(@PathVariable(value = "filmId") Long id) {
        log.info("Получен запрос DELETE на удаление фильма");
        Film film = filmService.removeFilm(id);
        log.info("Фильм c ID: {} удалён!", id);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Film addLike(@PathVariable(value = "id") Long filmId,
                        @PathVariable(value = "userId") Long userId) {
        log.info("Получен запрос PUT на добавление лайка. Id фильма: {}, Id пользователя: {}", filmId, userId);
        Film film = filmService.createLike(filmId, userId);
        log.info("Лайк успешно поставлен! Id фильма: {} ,Id пользователя: {}", filmId, userId);
        return film;

    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Film removeLike(@PathVariable(value = "id") Long filmId,
                           @PathVariable(value = "userId") Long userId) {
        log.info("Получен запрос DELETE на удаление лайка");
        Film film = filmService.removeLike(filmId, userId);
        log.info("Лайк пользователя {} успешно удалён!", userId);
        return film;
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getMostPopularsFilms(@RequestParam(defaultValue = "10") Integer count,
                                                 @RequestParam(required = false) Optional<Long> genreId,
                                                 @RequestParam(required = false) Optional<Integer> year) {
        log.info("Получен запрос GET на получение самых популярных фильмов!");
        log.info("Вывод {} популярных фильмов", count);
        return filmService.getMostPopularsFilms(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getFilmsByDirector(@PathVariable Long directorId,
                                               @RequestParam(required = false) Optional<SortParam[]> sortBy) {
        log.info("Получен запрос GET на получение фильмов по режисёру!");
        Collection<Film> films = filmService.getDirectorFilmsSorted(directorId, sortBy);
        log.info("Вывод {} фильмов данного режисёра", films.size());
        return films;
    }

    @GetMapping("/common")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getCommonFilms(@RequestParam Long userId,
                                           @RequestParam Long friendId) {
        log.info("Получен запрос GET для общих фильмов с другом по популярности");
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> searchFilmByParameter(@RequestParam(name = "query") String query,
                                                  @RequestParam(name = "by") FilmParameter filmSearchParameter) {
        log.info("Получен GET запрос на поиск: {}", filmSearchParameter);
        return filmService.searchFilmByParameter(query.toLowerCase(), filmSearchParameter);
    }
}
