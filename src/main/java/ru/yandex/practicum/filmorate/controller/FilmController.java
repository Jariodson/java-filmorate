package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
@ResponseStatus(HttpStatus.NOT_FOUND)
@Slf4j
public class FilmController {
    FilmService filmService = new FilmService();

    @GetMapping(value = {"", "/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getFilms(@PathVariable Optional<Long> id) {
        if (id.isPresent()){
            return List.of(filmService.getFilmById(id.get()));
        }else {
            return filmService.getFilms();
        }
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

    @GetMapping("/popular?count={count}")
    public Collection<Film> getFavouriteFilms(@RequestParam(defaultValue = "10") int count){
        return filmService.getFavouriteFilms(count);
    }



}
