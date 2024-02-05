package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testShouldReturnFilms() throws Exception {
        Film film1 =  new Film(1, "Aladdin", "Cartoon about prince",
                LocalDate.of(1967, 3, 25), 90);
        Film film2 = new Film(2, "Rusalochka", "Cartoon about the see princes",
                LocalDate.of(1985, 5, 12), 75);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        String gsonString = gson.toJson(List.of(film1, film2));

        FilmController filmController = new FilmController();
        filmController.addFilm(film1);
        filmController.addFilm(film2);

        this.mockMvc.perform(
                get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json(gsonString));
    }

    @Test
    void addFilm() {
    }

    @Test
    void updateFilm() {
    }
}