package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/users")
@ResponseStatus(HttpStatus.NOT_FOUND)
@Slf4j
public class UserController {
    UserService userService = new UserService();

    @GetMapping(value = {"", "{id}"})
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getUsers(@PathVariable Optional<Long> id) {
        if (id.isPresent()){
            return List.of(userService.findUserById(id.get()));
        }else {
            return userService.getUsers();
        }
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
    public ResponseEntity<User> deleteUser(@Valid @RequestBody User user){
        return userService.removeUser(user);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable Optional<Long> id){
        if (id.isPresent()){
            return userService.getFriends(id.get());
        }else {
            throw new IllegalArgumentException("Введён неверный индефикатор!");
        }
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable(value = "id") Optional<Long> userId,
                                          @PathVariable Optional<Long> friendId){
        if (userId.isPresent() && friendId.isPresent()){
            return userService.addFriend(userId.get(), friendId.get());
        }else {
            throw new IllegalArgumentException("Введён неверный индефикатор!");
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> removeFriend(@PathVariable(value = "id") Optional<Long> userId,
                                             @PathVariable Optional<Long> friendId){
        if (userId.isPresent() && friendId.isPresent()) {
            return userService.deleteFriend(userId.get(), friendId.get());
        }else {
            throw new IllegalArgumentException("Введён неверный индефикатор!");
        }
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable(value = "id") Optional<Long> userId,
                                             @PathVariable Optional<Long> friendId){
        if (userId.isPresent() && friendId.isPresent()){
            return userService.getCommonFriends(userId.get(), friendId.get());
        }else {
            throw new IllegalArgumentException("Введён неверный индефикатор!");
        }
    }
}
