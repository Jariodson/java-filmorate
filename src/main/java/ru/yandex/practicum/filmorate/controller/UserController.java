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
        log.info("Полчен запрос GET на получение всех пользователей");
        Collection<User> users = userService.getUsers();
        log.info("Вывод пользователей! Размер списка: {}", users.size());
        return users;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable Long id) {
        log.info("Получен запрос GET на получение пользователя с ID: {}", id);
        User user = userService.getUserById(id);
        log.info("Вывод пользоваля с Id: {}", id);
        return user;
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

    @DeleteMapping("/{userId}")
    public ResponseEntity<User> deleteUser(@PathVariable(value = "userId") Long id) {
        log.info("Получен запрос DELETE на удаление пользователя: {}", id);
        User user = userService.removeUser(id);
        log.info("Пользователя c ID {} успешно удалён!", id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getUserFriends(@PathVariable Long id) {
        log.info("Получен запрос GET на вывод всех друзей пользователя");
        Collection<User> friendId = userService.getFriends(id);
        log.info("Вывод друзей пользователя с Id: {}. Id друзей: {}", id, friendId);
        return friendId;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable(value = "id") Long userId,
                                          @PathVariable(value = "friendId") Long friendId) {
        log.info("Получен запрос PUT на добавление нового друга пользователя. " +
                "Id пользователя: {}, Id друга: {}", friendId, userId);
        User user = userService.addFriend(userId, friendId);
        log.info("Пользователь с Id: {} успешно добавил друга с Id: {}", friendId, userId);
        log.info("{}", user);
        return new ResponseEntity<>(user, HttpStatus.OK);

    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> removeFriend(@PathVariable(value = "id") Long userId,
                                             @PathVariable(value = "friendId") Long friendId) {

        log.info("Получен запрос DELETE на удаление пользователя из друзей" +
                "Id пользователя: {}, Id друга: {}", friendId, userId);
        User user = userService.deleteFriend(userId, friendId);
        log.info("Пользователь с Id: {} успешно удалил друга с Id: {}", friendId, userId);
        log.info("Пользователь: {}", user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getCommonFriends(@PathVariable(value = "id") Long userId,
                                             @PathVariable(value = "otherId") Long friendId) {
        log.info("Получен запрос GET на получение общих друзей пользователей: {} и {}", userId, friendId);
        Collection<User> commonFriends = userService.getCommonFriends(userId, friendId);
        log.info("Вывод общих друзей пользователя с Id: {} и Id: {}", userId, friendId);
        log.info("Общие друзья: {}", commonFriends);
        return commonFriends;
    }

    @GetMapping("/{id}/feed")
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserFeed> getCommonFriends(@PathVariable(value = "id") Long userId) {
        log.info("Получен запрос GET на получение истории пользователя: {} ", userId);
        Collection<UserFeed> userFeed = userService.getUserFeed(userId);
        log.info("Вывод общих истории действий пользователя с Id: {}", userId);
        return userFeed;
    }
}
