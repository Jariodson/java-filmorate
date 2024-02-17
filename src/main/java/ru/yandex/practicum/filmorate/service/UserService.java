package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    UserStorage userStorage = new InMemoryUserStorage();

    public Collection<User> getUsers() {
        log.info("Полчен запрос GET на получение всех пользователей");
        return userStorage.getAllUsers();
    }

    public ResponseEntity<User> createUser(User user) {
        log.info("Получен запрос POST на добавление пользователя в список");
        userStorage.addNewUser(user);
        log.info("Пользователь: {} добавлен!", user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    public ResponseEntity<User> updateUser(User user) {
        log.info("Получен запрос PUT на обновление пользователя в списке. Id пользователя: {}", user.getId());
        userStorage.updateUser(user);
        log.info("Информация о пользователе: {} обновлена!", user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<User> removeUser(User user) {
        log.info("Получен запрос DELETE на удаление пользователя: {}", user.getId());
        userStorage.deleteUser(user);
        log.info("Пользователя успешно удалён!");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public Collection<User> getFriends(long userId) {
        log.info("Получен запрос GET на вывод всех друзей пользователя");
        checkUserId(userId);
        User user = userStorage.getUserById(userId);
        return user.getFriendsIds().stream()
                .map(userFriend -> userStorage.getUserById(userFriend))
                .collect(Collectors.toList());
    }

    public ResponseEntity<User> addFriend(long userId, long friendId) {
        log.info("Получен запрос PUT на добавление нового друга пользователя. " +
                "Id пользователя: {}, Id друга: {}", userId, friendId);
        checkUserId(userId);
        checkUserId(friendId);
        User user = userStorage.getUserById(userId);
        if (userStorage.getAllUsers().stream().map(User::getId).anyMatch(id -> id.equals(userId)) &&
                userStorage.getAllUsers().stream().map(User::getId).anyMatch(id -> id.equals(friendId))) {
            user.createFriend(friendId);
            userStorage.getUserById(friendId).createFriend(userId);
        }
        log.info("Пользователь успешно добавил друга");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<User> deleteFriend(long userId, long friendId) {
        log.info("Получен запрос DELETE на удаление пользователя из друзей" +
                "Id пользователя: {}, Id друга: {}", userId, friendId);
        checkUserId(userId);
        checkUserId(friendId);
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.removeFriend(friendId);
        friend.removeFriend(userId);
        log.info("Пользователь успешно удалён из друзей!");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public Collection<User> getCommonFriends(long userId, long friendId) {
        log.info("Получен запрос GET на получение общих друзей пользователей: {} и {}", userId, friendId);
        checkUserId(userId);
        checkUserId(friendId);
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        List<User> commonFriends = new ArrayList<>();
        try {
            user.getFriendsIds().forEach(userFriendId -> {
                friend.getFriendsIds().stream().filter(userFriendId::equals)
                        .map(friendFrienId -> userStorage.getUserById(userFriendId))
                        .forEach(commonFriends::add);
            });
        }catch (NullPointerException e){
            return Collections.emptyList();
        }
        return commonFriends;
    }

    public User findUserById(Long id) {
        log.info("Получен запрос на поиск пользователя с ID: {}", id);
        checkUserId(id);
        if (userStorage.getUserById(id) == null){
            log.warn("Ошибка! Пользваотель с id {} не найден!", id);
            throw new IllegalArgumentException("Пользователь с ID " + id + " не найден!");
        }
        return userStorage.getUserById(id);
    }

    private void checkUserId(long id){
        if (id > 0){
            return;
        }
        log.warn("Ошибка! Введен неверный ID!");
        throw new IllegalArgumentException("Введен неверный ID!");
    }
}
