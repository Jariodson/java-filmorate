package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@ResponseStatus(HttpStatus.NOT_FOUND)
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getUsers() {
        log.info("Полчен запрос GET на получение всех пользователей");
        Collection<User> users = userService.getUsers();
        log.info("Вывод пользователей! Размер списка: {}", users.size());
        return users;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable Optional<Long> id) {
        if (id.isPresent()) {
            log.info("Получен запрос GET на получение пользователя с ID: {}", id);
            User user = userService.findUserById(id.get());
            log.info("Вывод пользоваля с Id: {}", id);
            return user;
        }
        throw new IllegalArgumentException("Введён неверный индефикатор! Id: " + id);
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        log.info("Получен запрос POST на добавление пользователя в список");
        userService.createUser(user);
        log.info("Пользователь: {} добавлен!", user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос PUT на обновление пользователя в списке. Id пользователя: {}", user.getId());
        userService.updateUser(user);
        log.info("Информация о пользователе: {} обновлена!", user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<User> deleteUser(@Valid @RequestBody User user) {
        log.info("Получен запрос DELETE на удаление пользователя: {}", user.getId());
        userService.removeUser(user);
        log.info("Пользователя успешно удалён!");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Long> getUserFriends(@PathVariable @NotNull Optional<Long> id) {
        log.info("Получен запрос GET на вывод всех друзей пользователя");
        if (id.isPresent()) {
            Collection<Long> friendId = userService.getFriends(id.get());
            log.info("Вывод друзей пользователя с Id: {}. Id друзей: {}", id.get(), friendId);
            return friendId;
        } else {
            throw new IllegalArgumentException("Введён неверный индефикатор! Id: " + id);
        }
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable(value = "id") @NotNull Optional<Long> userId,
                                          @PathVariable @NotNull Optional<Long> friendId) {
        if (userId.isPresent() && friendId.isPresent()) {
            log.info("Получен запрос PUT на добавление нового друга пользователя. " +
                    "Id пользователя: {}, Id друга: {}", userId, friendId);
            User user = userService.addFriend(userId.get(), friendId.get());
            log.info("Пользователь с Id: {} успешно добавил друга с Id: {}", userId, friendId);
            log.info("{}", user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("Введён неверный индефикатор! Id: " + userId + " или Id: " + friendId);
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> removeFriend(@PathVariable(value = "id") @NotNull Optional<Long> userId,
                                             @PathVariable @NotNull Optional<Long> friendId) {
        if (userId.isPresent() && friendId.isPresent()) {
            log.info("Получен запрос DELETE на удаление пользователя из друзей" +
                    "Id пользователя: {}, Id друга: {}", userId, friendId);
            User user = userService.deleteFriend(userId.get(), friendId.get());
            log.info("Пользователь с Id: {} успешно удалил друга с Id: {}", user, friendId);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("Введён неверный индефикатор! Id: " + userId + " или Id: " + friendId);
        }
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getCommonFriends(@PathVariable(value = "id") @NotNull Optional<Long> userId,
                                             @PathVariable(value = "otherId") @NotNull Optional<Long> friendId) {
        if (userId.isPresent() && friendId.isPresent()) {
            log.info("Получен запрос GET на получение общих друзей пользователей: {} и {}", userId, friendId);
            Collection<User> commonFriends = userService.getCommonFriends(userId.get(), friendId.get());
            log.info("Вывод общих друзей пользователя с Id: {} и Id: {}", userId, friendId);
            return commonFriends;
        } else {
            throw new IllegalArgumentException("Введён неверный индефикатор! Id: " + userId + " или Id: " + friendId);
        }
    }
}
