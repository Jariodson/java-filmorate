package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@Slf4j
public class UserService {
    UserStorage userStorage = new InMemoryUserStorage();
    public Collection<User> getUsers(){
        return userStorage.getAllUsers();
    }
    public ResponseEntity<User> createUser(User user){
        userStorage.addNewUser(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
    public ResponseEntity<User> updateUser(User user){
        userStorage.updateUser(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    public ResponseEntity<User> removeUser(User user){
        userStorage.deleteUser(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public Collection<Long> getFriends(Long userId) {
        log.info("Получен запрос GET на вывод всех друзей пользователя");
        return findUserById(userId).getFriendsIds();
    }

    public ResponseEntity<User> addFriend(Long userId, Long friendId) {
        log.info("Получен запрос POST на добавление нового друга пользователя. " +
                "Id пользователя: {}, Id друга: {}", userId, friendId);
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        user.addNewFriend(friendId);
        friend.addNewFriend(userId);
        log.info("Пользователь успешно добавил друга");
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    public ResponseEntity<User> deleteFriend(Long userId, Long friendId) {
        log.info("Получен запрос DELETE на удаление пользователя из друзей" +
                "Id пользователя: {}, Id друга: {}", userId, friendId);
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        user.removeFriend(friendId);
        friend.removeFriend(userId);
        log.info("Пользователь успешно удалён из друзей!");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    private User findUserById(Long id) {
        for (User user : userStorage.getAllUsers()) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        log.warn("Ошибка! Пользваотель с id {} не найден!", id);
        throw new IllegalArgumentException("Пользователь с ID " + id + " не найден!");
    }
}
