package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.validation.WithoutParent;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@RestController
@RequestMapping("/directors")
@Slf4j
public class DirectorController {

    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Director> getAllDirectors() {
        log.debug("Получен запрос GET на получение списка всех режисёров");
        Collection<Director> directors = directorService.getDirectors();
        log.debug("Вывод фильмов. Размер списка режисёров: {}", directors.size());
        return directors;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Director getDirectorById(@NotNull @PathVariable Long id) {
        log.debug("Получен запрос GET на получение режисёра по его id");
        Director director = directorService.getDirectorById(id);
        log.debug("Вывод режисёра. Имя режисёра: {}", director.getName());
        return director;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director addNewDirector(@Validated(WithoutParent.class)
                                   @RequestBody Director director) {
        log.debug("Получен запрос POST на добавление режисёра");
        directorService.createDirector(director);
        log.debug("Вывод режисёра. ID режисёра: {}", director.getId());
        return director;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Director updateDirector(@Validated(WithoutParent.class)
                                   @RequestBody Director director) {
        log.debug("Получен запрос PUT на обновление режисёра");
        directorService.updateDirector(director);
        log.debug("Вывод режисёра. ID режисёра: {}  Имя режисёра: {}", director.getId(), director.getName());
        return director;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDirector(@NotNull @PathVariable Long id) {
        log.debug("Получен запрос DELETE на удоление режисёра");
        directorService.deleteDirector(id);
    }
}