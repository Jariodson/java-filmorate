package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFeed;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getUsers() {
        log.debug("Полчен запрос GET на получение всех пользователей");
        Collection<User> users = userService.getUsers();
        log.debug("Вывод пользователей! Размер списка: {}", users.size());
        return users;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@NotNull @PathVariable Long id) {
        log.debug("Получен запрос GET на получение пользователя с ID: {}", id);
        User user = userService.getUserById(id);
        log.debug("Вывод пользоваля с Id: {}", id);
        return user;
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        log.debug("Получен запрос POST на добавление пользователя в список");
        userService.createUser(user);
        log.debug("Пользователь: {} добавлен!", user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.debug("Получен запрос PUT на обновление пользователя в списке. Id пользователя: {}", user.getId());
        userService.updateUser(user);
        log.debug("Информация о пользователе: {} обновлена!", user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<User> deleteUser(@NotNull @PathVariable(value = "userId") Long id) {
        log.debug("Получен запрос DELETE на удаление пользователя: {}", id);
        User user = userService.removeUser(id);
        log.debug("Пользователя c ID {} успешно удалён!", id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getUserFriends(@NotNull @PathVariable Long id) {
        log.debug("Получен запрос GET на вывод всех друзей пользователя");
        Collection<User> friendId = userService.getFriends(id);
        log.debug("Вывод друзей пользователя с Id: {}. Id друзей: {}", id, friendId);
        return friendId;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@NotNull @PathVariable(value = "id") @NotNull Long userId,
                                          @NotNull @PathVariable @NotNull Long friendId) {
        log.debug("Получен запрос PUT на добавление нового друга пользователя. " +
                "Id пользователя: {}, Id друга: {}", friendId, userId);
        User user = userService.addFriend(friendId, userId);
        log.debug("Пользователь с Id: {} успешно добавил друга с Id: {}", friendId, userId);
        log.debug("{}", user);
        return new ResponseEntity<>(user, HttpStatus.OK);

    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> removeFriend(@NotNull @PathVariable(value = "id") @NotNull Long userId,
                                             @NotNull @PathVariable @NotNull Long friendId) {

        log.debug("Получен запрос DELETE на удаление пользователя из друзей" +
                "Id пользователя: {}, Id друга: {}", friendId, userId);
        User user = userService.deleteFriend(friendId, userId);
        log.debug("Пользователь с Id: {} успешно удалил друга с Id: {}", friendId, userId);
        log.debug("Пользователь: {}", user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getCommonFriends(@NotNull @PathVariable(value = "id") @NotNull Long userId,
                                             @NotNull @PathVariable(value = "otherId") @NotNull Long friendId) {
        log.debug("Получен запрос GET на получение общих друзей пользователей: {} и {}", userId, friendId);
        Collection<User> commonFriends = userService.getCommonFriends(userId, friendId);
        log.debug("Вывод общих друзей пользователя с Id: {} и Id: {}", userId, friendId);
        log.debug("Общие друзья: {}", commonFriends);
        return commonFriends;
    }

    @GetMapping("/{id}/feed")
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserFeed> getCommonFriends(@NotNull @PathVariable(value = "id") @NotNull Long userId) {
        log.debug("Получен запрос GET на получение истории пользователя: {} ", userId);
        Collection<UserFeed> userFeed = userService.getUserFeed(userId);
        log.debug("Вывод общих истории действий пользователя с Id: {}", userId);
        return userFeed;
    }
}
