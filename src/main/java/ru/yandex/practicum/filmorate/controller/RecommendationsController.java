package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.RecommendationsService;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecommendationsController {
    private final RecommendationsService service;

    @Autowired
    public RecommendationsController(RecommendationsService service) {
        this.service = service;
    }

    @GetMapping("/{id}/recommendations")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getUserRecommendations(@PathVariable Optional<Long> id) {
        if (id.isPresent()) {
            log.info("Получен запрос GET на получение рекомендаций для пользователя с ID: {}", id.get());
            Collection<Film> films = service.getRecommendations(id.get());
            log.info("Вывод рекомендованных фильмов для пользователя с ID: {}", id.get());
            return films;
        }
        throw new IllegalArgumentException("Введен неверный идентефикатор! ID: " + id);
    }
}
