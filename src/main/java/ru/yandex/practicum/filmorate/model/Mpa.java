package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.WithoutParent;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
public class Mpa {
    private Long id;
    @NotEmpty(groups = WithoutParent.class)
    private String name;
}
