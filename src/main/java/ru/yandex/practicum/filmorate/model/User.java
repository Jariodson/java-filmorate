package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.Enums.StatuseForFriendsCommunication;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class User {
    @Email(message = "Введён некоректный e-mail")
    private final String email;
    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    private final String login;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate birthday;
    private final Set<Long> friendsIds = new HashSet<>();
    private Long id;
    private String name;
    private Map<Long, StatuseForFriendsCommunication> status;

    public void createFriend(long id) {
        friendsIds.add(id);
    }

    public void removeFriend(long id) {
        friendsIds.remove(id);
    }
}
