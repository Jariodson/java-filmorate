package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/mpa")
@ResponseStatus(HttpStatus.NOT_FOUND)
public class MpaController {
    @Autowired
    private MpaService mpaService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Mpa> getGenres(){
        log.info("Получен запрос GET на вывод всех рейтингов");
        Collection<Mpa> mpaCollection = mpaService.getMpa();
        log.info("Вывод списка всех рейтингов. Размер списка: {}", mpaCollection.size());
        return mpaCollection;
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mpa getGenreById(@PathVariable Optional<Long> id){
        log.info("Получен запрос GET на получение рейтинга по ID: {}", id);
        if (id.isPresent()){
            Mpa mpa = mpaService.getMpaById(id.get());
            log.info("Вывод рейтинга с Id: {}", id);
            return mpa;
        }
        throw new IllegalArgumentException("Введен неверный индефикатор! Id: " + id);
    }
}
