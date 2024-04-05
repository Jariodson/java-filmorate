package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface RecommendationsService {
    Collection<Film> getRecommendations(Long id);
}
