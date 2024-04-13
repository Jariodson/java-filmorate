package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<Review> makeReview(@Valid @RequestBody Review review) {
        log.info("Получен запрос POST на добовление нового отзыва");
        Review newReview = reviewService.createReview(review);
        log.info("Отзыв сохранён!");
        return new ResponseEntity<>(newReview, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Review> updateReview(@Valid @RequestBody Review review) {
        log.info("Получен запрос UPDATE на обновление отзыва");
        Review newReview = reviewService.updateReview(review);
        log.info("Отзыв с ID: {} успешно обновлён!", review.getReviewId());
        return new ResponseEntity<>(newReview, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Review> deleteReview(@PathVariable Long id) {
        log.info("Получен запрос DELETE на удаление отзыва");
        Review review = reviewService.deleteReview(id);
        log.info("Отзыв с ID: {} успешно удалён!", id);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        log.info("Получен запрос GET на получение отзыва с ID: {}", id);
        Review review = reviewService.getReviewById(id);
        log.info("Вывод отзыва с ID: {}", id);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Review> getReviewsByFilmIdAndCount(@RequestParam(required = false) Long filmId,
                                                         @RequestParam(defaultValue = "10") Integer count) {
        if (filmId == null) {
            log.info("Получен запрос GET на получение отзывов ко всем фильмам. Кол-во отзывов: {}", count);
            Collection<Review> reviews = reviewService.getAllReviews(count);
            log.info("Вывод {} отзывов к фильмам.", reviews.size());
            return reviews;
        } else if (filmId > 0) {
            log.info("Получен запрос GET на получение отзыва к фильму с ID: {}. Кол-во отзывов: {}", filmId,
                    count);
            Collection<Review> reviews = reviewService.getReviewByFilmId(filmId, count);
            log.info("Вывод отзывов к фильму с ID: {}", filmId);
            return reviews;
        } else {
            throw new IllegalArgumentException("Введён неверный индефикатор фильма! Id: " + filmId);
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Review> postLike(@PathVariable(value = "id") Long id,
                                           @PathVariable(value = "userId") Long userId) {
        log.info("Получен запрос PUT на добавление лайка отзыву с ID: {} пользователем с ID: {}",
                id, userId);
        Review review = reviewService.postLike(id, userId);
        log.info("Пользователь с ID: {} успешно поставил лайк посту с ID: {}", userId, id);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ResponseEntity<Review> postDislike(@PathVariable(value = "id") Long id,
                                              @PathVariable(value = "userId") Long userId) {
        log.info("Получен запрос PUT на добавление дизлайка отзыву с ID: {} пользователем с ID: {}",
                id, userId);
        Review review = reviewService.postDislike(id, userId);
        log.info("Пользователь с ID: {} успешно поставил дизлайк посту с ID: {}", userId, id);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Review> deleteLike(@PathVariable(value = "id") Long id,
                                             @PathVariable(value = "userId") Long userId) {
        log.info("Получен запрос DELETE на удаление лайка отзыву с ID: {} пользователем с ID: {}",
                id, userId);
        Review review = reviewService.deleteLike(id, userId);
        log.info("Пользователь с ID: {} успешно удлалил лайк посту с ID: {}", userId, id);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ResponseEntity<Review> deleteDislike(@PathVariable(value = "id") Long id,
                                                @PathVariable(value = "userId") Long userId) {
        log.info("Получен запрос DELETE на удаление дизлайка отзыву с ID: {} пользователем с ID: {}",
                id, userId);
        Review review = reviewService.deleteDislike(id, userId);
        log.info("Пользователь с ID: {} успешно удлалил дизлайк посту с ID: {}", userId, id);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }
}
