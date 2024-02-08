package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@ResponseStatus(HttpStatus.NOT_FOUND)
@Slf4j
public class UserController {
    private static int genId = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        setGenId(user);
        log.info("Получен запрос POST на добавление пользователя в список");
        checkUserCriteria(user);
        if (users.values().stream().map(User::getEmail).anyMatch(user.getEmail()::equals)) {
            log.warn("Пользователь с e-mail: {} уже зарегестрирован!", user.getEmail());
            throw new ValidationException("Пользователь с электронной почтой " +
                    user.getEmail() + " уже зарегистрирован.");
        }
        users.put(user.getId(), user);
        log.info("Пользователь: {} добавлен!", user);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        setGenId(user);
        log.info("Получен запрос PUT на обновление пользователя в списке. Id пользователя: {}", user.getId());
        if (users.containsKey(user.getId())) {
            checkUserCriteria(user);
            users.put(user.getId(), user);
            log.info("Информация о пользователе: {} обновлена!", user);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        }
        log.warn("Пользователь не найден в списке!");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(user);
    }

    private void checkUserCriteria(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Введено пустое имя, поэтому имя изменено на логин: {}", user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now().plusDays(1))) {
            log.warn("Введена неправильная дата рождения: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private void setGenId(User user) {
        if (user.getId() == 0) {
            user.setId(++genId);
        }
    }
}
