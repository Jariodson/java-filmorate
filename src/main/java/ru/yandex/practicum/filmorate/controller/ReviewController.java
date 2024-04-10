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
import java.util.Optional;

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
        log.debug("Получен запрос POST на добовление нового отзыва");
        Review newReview = reviewService.createReview(review);
        System.out.println(newReview);
        log.debug("Отзыв сохранён!");
        return new ResponseEntity<>(newReview, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Review> updateReview(@Valid @RequestBody Review review) {
        log.debug("Получен запрос UPDATE на обновление отзыва");
        Review newReview = reviewService.updateReview(review);
        log.debug("Отзыв с ID: {} успешно обновлён!", review.getReviewId());
        return new ResponseEntity<>(newReview, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Review> deleteReview(@PathVariable Optional<Long> id) {
        if (id.isPresent()) {
            log.debug("Получен запрос DELETE на удаление отзыва");
            Review review = reviewService.deleteReview(id.get());
            log.debug("Отзыв с ID: {} успешно удалён!", id.get());
            return new ResponseEntity<>(review, HttpStatus.OK);
        }
        throw new IllegalArgumentException("Введён неверный индефикатор! Id: " + id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Optional<Long> id) {
        if (id.isPresent()) {
            log.debug("Получен запрос GET на получение отзыва с ID: {}", id.get());
            Review review = reviewService.getReviewById(id.get());
            log.debug("Вывод отзыва с ID: {}", id.get());
            return new ResponseEntity<>(review, HttpStatus.OK);
        }
        throw new IllegalArgumentException("Введён неверный индефикатор! Id: " + id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Review> getReviewsByFilmIdAndCount(@RequestParam(defaultValue = "0") String filmId,
                                                         @RequestParam(defaultValue = "10") String count) {
        long id = Long.parseLong(filmId);
        int reviewsCount = Integer.parseInt(count);
        if (id > 0) {
            log.debug("Получен запрос GET на получение отзыва к фильму с ID: {}. Кол-во отзывов: {}", filmId,
                    reviewsCount);
            Collection<Review> reviews = reviewService.getReviewByFilmId(id, reviewsCount);
            log.debug("Вывод отзывов к фильму с ID: {}", filmId);
            return reviews;
        } else if (id == 0) {
            log.debug("Получен запрос GET на получение отзывов ко всем фильмам. Кол-во отзывов: {}", reviewsCount);
            Collection<Review> reviews = reviewService.getAllReviews(reviewsCount);
            log.debug("Вывод {} отзывов к фильмам.", reviews.size());
            return reviews;
        }
        throw new IllegalArgumentException("Введён неверный индефикатор фильма! Id: " + filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Review> postLike(@PathVariable(value = "id") Optional<Long> id,
                                           @PathVariable(value = "userId") Optional<Long> userId) {
        if (id.isPresent() && userId.isPresent()) {
            log.debug("Получен запрос PUT на добавление лайка отзыву с ID: {} пользователем с ID: {}",
                    id.get(), userId.get());
            Review review = reviewService.postLike(id.get(), userId.get());
            log.debug("Пользователь с ID: {} успешно поставил лайк посту с ID: {}", userId.get(), id.get());
            return new ResponseEntity<>(review, HttpStatus.OK);
        }
        throw new IllegalArgumentException("Введён неверный индефикатор! Id: " + id + " или UserId: " + userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ResponseEntity<Review> postDislike(@PathVariable(value = "id") Optional<Long> id,
                                              @PathVariable(value = "userId") Optional<Long> userId) {
        if (id.isPresent() && userId.isPresent()) {
            log.debug("Получен запрос PUT на добавление дизлайка отзыву с ID: {} пользователем с ID: {}",
                    id.get(), userId.get());
            Review review = reviewService.postDislike(id.get(), userId.get());
            log.debug("Пользователь с ID: {} успешно поставил дизлайк посту с ID: {}", userId.get(), id.get());
            return new ResponseEntity<>(review, HttpStatus.OK);
        }
        throw new IllegalArgumentException("Введён неверный индефикатор! Id: " + id + " или UserId: " + userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Review> deleteLike(@PathVariable(value = "id") Optional<Long> id,
                                             @PathVariable(value = "userId") Optional<Long> userId) {
        if (id.isPresent() && userId.isPresent()) {
            log.debug("Получен запрос DELETE на удаление лайка отзыву с ID: {} пользователем с ID: {}",
                    id.get(), userId.get());
            Review review = reviewService.deleteLike(id.get(), userId.get());
            log.debug("Пользователь с ID: {} успешно удлалил лайк посту с ID: {}", userId.get(), id.get());
            return new ResponseEntity<>(review, HttpStatus.OK);
        }
        throw new IllegalArgumentException("Введён неверный индефикатор! Id: " + id + " или UserId: " + userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ResponseEntity<Review> deleteDislike(@PathVariable(value = "id") Optional<Long> id,
                                                @PathVariable(value = "userId") Optional<Long> userId) {
        if (id.isPresent() && userId.isPresent()) {
            log.debug("Получен запрос DELETE на удаление дизлайка отзыву с ID: {} пользователем с ID: {}",
                    id.get(), userId.get());
            Review review = reviewService.deleteDislike(id.get(), userId.get());
            log.debug("Пользователь с ID: {} успешно удлалил дизлайк посту с ID: {}", userId.get(), id.get());
            return new ResponseEntity<>(review, HttpStatus.OK);
        }
        throw new IllegalArgumentException("Введён неверный индефикатор! Id: " + id + " или UserId: " + userId);
    }
}
