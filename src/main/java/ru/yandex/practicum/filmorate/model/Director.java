package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.WithoutParent;

import javax.validation.constraints.NotBlank;


@Data
@Builder
@AllArgsConstructor
public class Director {

    private Long id;
    @NotBlank(groups = WithoutParent.class)
    private String name;
}

