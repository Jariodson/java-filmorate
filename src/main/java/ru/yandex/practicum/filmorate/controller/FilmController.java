package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

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
        log.debug("Получен запрос GET на получение списка всех фильмов");
        Collection<Film> films = filmService.getFilms();
        log.debug("Вывод фильмов. Размер списка фильмов: {}", films.size());
        return films;
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film getFilmById(@NotNull @PathVariable Long id) {
        log.debug("Получен запрос GET на получение фильма по ID: {}", id);
        Film film = filmService.getFilmById(id);
        log.debug("Вывод фильма с Id: {}", id);
        return film;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) {
        log.debug("Получен запрос POST на добавление фильма в список");
        Film newFilm = filmService.createFilm(film);
        log.debug("Фильм добавлен в список: {}. Размер списка: {}", film, filmService.getFilms().size());
        return newFilm;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Получен запрос PUT на обновления фильма в списке");
        Film newFilm = filmService.updateFilm(film);
        log.debug("Обновленный фильм: {} добавлен в список. Размер списка: {}", film, filmService.getFilms().size());
        return newFilm;
    }

    @DeleteMapping("{filmId}")
    @ResponseStatus(HttpStatus.OK)
    public Film deleteFilm(@NotNull @PathVariable(value = "filmId") Long id) {
        log.debug("Получен запрос DELETE на удаление фильма");
        Film film = filmService.removeFilm(id);
        log.debug("Фильм c ID: {} удалён!", id);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Film addLike(@NotNull @PathVariable(value = "id") Long filmId,
                        @NotNull @PathVariable Long userId) {
        log.debug("Получен запрос PUT на добавление лайка. Id фильма: {}, Id пользователя: {}", filmId, userId);
        Film film = filmService.createLike(filmId, userId);
        log.debug("Лайк успешно поставлен! Id фильма: {} ,Id пользователя: {}", filmId, userId);
        return film;

    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Film removeLike(@NotNull @PathVariable(value = "id") Long filmId,
                           @NotNull @PathVariable Long userId) {
        log.debug("Получен запрос DELETE на удаление лайка");
        Film film = filmService.removeLike(filmId, userId);
        log.debug("Лайк пользователя {} успешно удалён!", userId);
        return film;
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getMostPopularsFilms(@RequestParam(defaultValue = "10") Integer count,
                                                 @RequestParam(required = false) Long genreId,
                                                 @RequestParam(required = false) Integer year) {
        log.info("Получен запрос GET на получение самых популярных фильмов!");
        log.info("Вывод {} популярных фильмов", count);
        return filmService.getMostPopularsFilms(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getFilmsByDirector(@NotNull @PathVariable Long directorId,
                                               @RequestParam(required = false) String[] sortBy) {
        log.debug("Получен запрос GET на получение фильмов по режисёру!");
        Collection<Film> films = filmService.getDirectorFilmsSorted(directorId, sortBy);
        log.debug("Вывод {} фильмов данного режисёра", films.size());
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
    public List<Film> searchFilmByParameter(@RequestParam(name = "query") String query,
                                            @RequestParam(name = "by") String filmSearchParameter) {
        log.info("Получен GET запрос на поиск");
        return filmService.searchFilmByParameter(query.toLowerCase(), filmSearchParameter.toLowerCase());
    }
}
