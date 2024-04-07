package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Genre> getGenres() {
        log.debug("Получен запрос GET на вывод всех жанров");
        Collection<Genre> genres = genreService.getGenres();
        log.debug("Вывод списка всех жанров. Размер списка: {}", genres.size());
        return genres;
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Genre getGenreById(@NotNull @PathVariable Long id) {
        log.debug("Получен запрос GET на получение жанра по ID: {}", id);
        Genre genre = genreService.getGenreById(id);
        log.debug("Вывод жанра с Id: {}", id);
        return genre;
    }
}
