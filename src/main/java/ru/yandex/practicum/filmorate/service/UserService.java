package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    UserStorage userStorage = new InMemoryUserStorage();
    public Collection<User> getUsers(){
        log.info("Полчен запрос GET на получение всех пользователей");
        return userStorage.getAllUsers();
    }
    public ResponseEntity<User> createUser(User user){
        log.info("Получен запрос POST на добавление пользователя в список");
        userStorage.addNewUser(user);
        log.info("Пользователь: {} добавлен!", user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
    public ResponseEntity<User> updateUser(User user){
        log.info("Получен запрос PUT на обновление пользователя в списке. Id пользователя: {}", user.getId());
        userStorage.updateUser(user);
        log.info("Информация о пользователе: {} обновлена!", user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    public ResponseEntity<User> removeUser(User user){
        log.info("Получен запрос DELETE на удаление пользователя: {}", user.getId());
        userStorage.deleteUser(user);
        log.info("Пользователя успешно удалён!");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public Collection<User> getFriends(Long userId) {
        log.info("Получен запрос GET на вывод всех друзей пользователя");
        User user = findUserById(userId);
        return user.getFriendsIds().stream().map(this::findUserById).collect(Collectors.toList());
    }

    public ResponseEntity<User> addFriend(long userId, long friendId) {
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
    public Collection<User> getCommonFriends(Long userId, Long friendId){
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        List<User> commonFriends = new ArrayList<>();
        user.getFriendsIds().forEach(userFriendId -> friend.getFriendsIds().stream().filter(userFriendId::equals)
                .map(this::findUserById).forEach(commonFriends::add));
        return commonFriends;
    }

    public User findUserById(Long id) {
        for (User user : userStorage.getAllUsers()) {
            if (user.getId() == id) {
                return user;
            }
        }
        log.warn("Ошибка! Пользваотель с id {} не найден!", id);
        throw new IllegalArgumentException("Пользователь с ID " + id + " не найден!");
    }
}
