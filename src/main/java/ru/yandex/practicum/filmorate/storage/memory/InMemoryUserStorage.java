package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private static long genId = 0;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public void addNewUser(User user) {
        checkUserCriteria(user);
        if (users.values().stream().map(User::getEmail).anyMatch(user.getEmail()::equals)) {
            throw new ValidationException("Пользователь с электронной почтой " +
                    user.getEmail() + " уже зарегистрирован.");
        }
        user.setId(++genId);
        users.put(user.getId(), user);
    }

    @Override
    public void updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new IllegalArgumentException("Пользователь с Id: " + user.getId() + " не найден в списке!");
        }
        checkUserCriteria(user);
        users.put(user.getId(), user);
    }

    @Override
    public void deleteUser(Long id) {
        if (!users.containsKey(id)) {
            throw new IllegalArgumentException("Такого пользователя не существует!");
        }
        users.remove(id);
    }

    @Override
    public Collection<User> getFriends(long userId) {
        return null;
    }

    @Override
    public User getUserById(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        throw new IllegalArgumentException("Пользователь с Id " + id + " не найден");
    }

    @Override
    public User addFriend(long userId, long friendId) {
        return null;
    }

    @Override
    public User deleteFriend(long userId, long friendId) {
        return null;
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long friendId) {
        return null;
    }

    private void checkUserCriteria(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Введено пустое имя, поэтому имя изменено на логин: {}", user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now().plusDays(1))) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
