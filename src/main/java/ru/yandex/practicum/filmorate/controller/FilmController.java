package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
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

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film getFilmById(@PathVariable Optional<Long> id) {
        if (id.isPresent()) {
            return filmService.getFilmById(id.get());
        }
        throw new IllegalArgumentException("Введен неверные индефикатор!");
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @DeleteMapping
    public ResponseEntity<Film> deleteFilm(@Valid @RequestBody Film film) {
        return filmService.deleteFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable(value = "id") Optional<Long> filmId,
                                        @PathVariable Optional<Long> userId) {
        if (filmId.isPresent() && userId.isPresent()) {
            return filmService.addLike(filmId.get(), userId.get());
        } else {
            throw new IllegalArgumentException("Введены неверные индефикаторы!");
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> removeLike(@PathVariable(value = "id") Optional<Long> filmId,
                                           @PathVariable Optional<Long> userId) {
        if (filmId.isPresent() && userId.isPresent()) {
            return filmService.removeLike(filmId.get(), userId.get());
        } else {
            throw new IllegalArgumentException("Введены неверные индефикаторы!");
        }
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getFavouriteFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getFavouriteFilms(count);
    }
}
