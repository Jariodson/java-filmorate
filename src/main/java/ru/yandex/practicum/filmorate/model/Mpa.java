package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Mpa {
    private Long id;
    @NotEmpty
    private String name;
}
