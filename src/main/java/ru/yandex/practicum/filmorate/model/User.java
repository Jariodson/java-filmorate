package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;

@Data
public class User {
    private final int id;
    @Email
    private final String email;
    private final String login;
    private String name;
    private final LocalDate birthday;
}
