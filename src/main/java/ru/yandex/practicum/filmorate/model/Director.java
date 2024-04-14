package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.WithoutParent;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Director {

    private Long id;
    @NotBlank
    private String name;
}

