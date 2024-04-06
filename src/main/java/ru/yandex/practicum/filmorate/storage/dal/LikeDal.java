package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface LikeDal {

    void addLike(Long filmId, Long userId) ;

    void removeLike(Long filmId, Long userId);

    Collection<Long> getLikes(Long filmId);
    Collection<Long> getPopularFilmsId(int size);
    Long getLikesAmount(Long filmId);
}
