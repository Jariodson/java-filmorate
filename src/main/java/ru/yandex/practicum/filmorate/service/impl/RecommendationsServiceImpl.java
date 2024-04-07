package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.RecommendationsService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@Service
public class RecommendationsServiceImpl implements RecommendationsService {
    private final UserService userService;
    private final FilmService filmService;
    Map<User, ArrayList<Film>> data;

    @Autowired
    public RecommendationsServiceImpl(UserService userService, FilmService filmService) {
        this.userService = userService;
        this.filmService = filmService;
        data = new HashMap<>();
    }

    @Override
    public Collection<Film> getRecommendations(Long id) {
        Collection<Film> films = filmService.getFilms();
        Collection<User> users = userService.getUsers();
        films.forEach(film1 -> users.stream().filter(user1 -> film1.getLikes().contains(user1.getId())).forEach(user1 -> {
            if (!data.containsKey(user1)) {
                data.put(user1, new ArrayList<>());
            }
            data.get(user1).add(film1);
        }));

        User targerUser = userService.getUserById(id);
        List<Film> recommendedFilms = new ArrayList<>();

        boolean anyLikes = data.values().stream().anyMatch(list -> !list.isEmpty());
        if (!anyLikes) {
            return recommendedFilms;
        }

        int maxIntersection = 0;
        User similarUser = null;
        Set<Film> targetUserLikes = new HashSet<>(data.get(targerUser));
        for (Map.Entry<User, ArrayList<Film>> entry : data.entrySet()) {
            if (!entry.getKey().equals(targerUser)) {
                Set<Film> intersection = new HashSet<>(entry.getValue());
                intersection.retainAll(targetUserLikes);
                if (intersection.size() > maxIntersection) {
                    maxIntersection = intersection.size();
                    similarUser = entry.getKey();
                }
            }
        }

        if (similarUser != null) {
            Set<Film> similarUserLikes = new HashSet<>(data.get(similarUser));
            for (Film film : similarUserLikes) {
                if (!targetUserLikes.contains(film)) {
                    recommendedFilms.add(film);
                }
            }
        }

        data.clear();
        return recommendedFilms;
    }
}
