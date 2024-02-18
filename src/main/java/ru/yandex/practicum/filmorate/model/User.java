package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    @Email(message = "Введён некоректный e-mail")
    private final String email;
    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    private final String login;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate birthday;
    private long id;
    private String name;
    private Set<Long> friendsIds = new HashSet<>();

    public void createFriend(long id) {
        if (friendsIds == null) {
            friendsIds = new HashSet<>();
        }
        friendsIds.add(id);
    }

    public void removeFriend(long id) {
        friendsIds.remove(id);
    }
}