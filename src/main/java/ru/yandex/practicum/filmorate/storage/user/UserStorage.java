package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAllUsers();
    void addNewUser(User user);
    void updateUser(User user);
    void deleteUser(User user);
}
