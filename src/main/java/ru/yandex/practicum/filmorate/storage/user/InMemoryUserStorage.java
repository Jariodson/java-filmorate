package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

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
            log.warn("Пользователь с e-mail: {} уже зарегестрирован!", user.getEmail());
            throw new ValidationException("Пользователь с электронной почтой " +
                    user.getEmail() + " уже зарегистрирован.");
        }
        users.put(user.getId(), user);
    }

    @Override
    public void updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь не найден в списке!");
            throw new IllegalArgumentException("Пользователь не найден в списке!");
        }
        checkUserCriteria(user);
        users.put(user.getId(), user);
    }

    @Override
    public void deleteUser(User user) {
        if (!users.containsValue(user)) {
            log.warn("Ошибка! Такого пользователя не существует!");
            throw new IllegalArgumentException("Такого пользователя не существует!");
        }
        users.remove(user.getId());
    }

    private void checkUserCriteria(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Введено пустое имя, поэтому имя изменено на логин: {}", user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now().plusDays(1))) {
            log.warn("Введена неправильная дата рождения: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getId() == 0) {
            user.setId(++genId);
            log.info("Пользователю присвоен ID: {}", user.getId());
        }
    }
}
