package ru.yandex.practicum.filmorate.storage.dal.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dal.LikeDal;

import java.util.Collection;

@Repository
@Slf4j
public class LikeDao implements LikeDal {
    private final JdbcTemplate jdbcTemplate;

    public LikeDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_like (film_id, user_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sql, filmId, userId);
            log.info("Пользователь {} добавил лайк фильму {}", userId, filmId);
        } catch (Exception e) {
            log.error("Ошибка при добавлении лайка", e);
            throw new RuntimeException("Ошибка при добавлении лайка");
        }
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";
        try {
            jdbcTemplate.update(sql, filmId, userId);
            log.info("Пользователь {} удалил лайк у фильма {}", userId, filmId);
        } catch (Exception e) {
            log.error("Ошибка при удалении лайка", e);
            throw new RuntimeException("Ошибка при удалении лайка");
        }
    }

    @Override
    public Collection<Long> getLikes(Long filmId) {
        String sql = "SELECT user_id FROM film_like WHERE film_id = ?";
        try {
            Collection<Long> userIds = jdbcTemplate.queryForList(sql, Long.class, filmId);
            log.info("Успешно извлечены лайки для фильма {}", filmId);
            return userIds;
        } catch (Exception e) {
            log.error("Ошибка при извлечении лайков", e);
            throw new RuntimeException("Ошибка при извлечении лайков");
        }
    }

    @Override
    public Long getLikesAmount(Long filmId) {
        String sql = "SELECT COUNT(*) FROM film_like WHERE film_id = ?";
        try {
            Long count = jdbcTemplate.queryForObject(sql, Long.class, filmId);
            log.info("Количество лайков для фильма {}: {}", filmId, count);
            return count;
        } catch (Exception e) {
            log.error("Ошибка при получении количества лайков", e);
            throw new RuntimeException("Ошибка при получении количества лайков");
        }
    }

    @Override
    public Collection<Long> getPopularFilmsId(int size) {
        String sql = "SELECT film_id FROM film_like GROUP BY film_id ORDER BY COUNT(*) DESC LIMIT ?";
        try {
            Collection<Long> popularFilmsIds = jdbcTemplate.queryForList(sql, Long.class, size);
            log.info("Успешно получены популярные фильмы");
            return popularFilmsIds;
        } catch (Exception e) {
            log.error("Ошибка при получении популярных фильмов", e);
            throw new RuntimeException("Ошибка при получении популярных фильмов");
        }
    }
}