package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@ResponseStatus(HttpStatus.NOT_FOUND)
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable Optional<Long> id) {
        if (id.isPresent()) {
            return userService.findUserById(id.get());
        }
        throw new IllegalArgumentException("Введён неверный индефикатор!");
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping
    public ResponseEntity<User> deleteUser(@Valid @RequestBody User user) {
        return userService.removeUser(user);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getUserFriends(@PathVariable Optional<Long> id) {
        if (id.isPresent()) {
            return userService.getFriends(id.get());
        } else {
            throw new IllegalArgumentException("Введён неверный индефикатор!");
        }
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable(value = "id") Optional<Long> userId,
                                          @PathVariable Optional<Long> friendId) {
        if (userId.isPresent() && friendId.isPresent()) {
            return userService.addFriend(userId.get(), friendId.get());
        } else {
            throw new IllegalArgumentException("Введён неверный индефикатор!");
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> removeFriend(@PathVariable(value = "id") Optional<Long> userId,
                                             @PathVariable Optional<Long> friendId) {
        if (userId.isPresent() && friendId.isPresent()) {
            return userService.deleteFriend(userId.get(), friendId.get());
        } else {
            throw new IllegalArgumentException("Введён неверный индефикатор!");
        }
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getCommonFriends(@PathVariable(value = "id") Optional<Long> userId,
                                             @PathVariable(value = "otherId") Optional<Long> friendId) {
        if (userId.isPresent() && friendId.isPresent()) {
            return userService.getCommonFriends(userId.get(), friendId.get());
        } else {
            throw new IllegalArgumentException("Введён неверный индефикатор!");
        }
    }
}
