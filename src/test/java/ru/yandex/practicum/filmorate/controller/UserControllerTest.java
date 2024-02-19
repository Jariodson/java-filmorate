package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Autowired
    private static ObjectMapper objectMapper;
    private MockMvc mockMvc;
    @InjectMocks
    private UserController controller;

    @Mock
    private UserService userService;

    @BeforeEach
    void beforeEach() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetUsersShouldReturnUsers() throws Exception {
        User user1 = User.builder()
                .id(1L)
                .name("Alex")
                .email("spring.a@yandex.ru")
                .login("alexSpring")
                .birthday(LocalDate.parse("1998-03-25"))
                .build();
        User user2 = User.builder()
                .id(1L)
                .name("Joseph")
                .email("winter.j@yandex.ru")
                .login("josephWinter")
                .birthday(LocalDate.parse("1989-03-25"))
                .build();
        List<User> users = Arrays.asList(user1, user2);

        String gsonString = objectMapper.writeValueAsString(users);

        when(userService.getUsers()).thenReturn(users);
        this.mockMvc.perform(
                        get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(gsonString))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testAddUserShouldReturnNewUser() throws Exception {
        User user1 = User.builder()
                .name("Alex")
                .email("spring.a@yandex.ru")
                .login("alexSpring")
                .birthday(LocalDate.parse("1998-03-25"))
                .build();
        when(userService.createUser(user1)).thenReturn(user1);
        this.mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdateUserShouldReturnUserWithStatusOk() throws Exception {
        User user1 = User.builder()
                .id(1L)
                .name("Alex")
                .email("spring.a@yandex.ru")
                .login("alexSpring")
                .birthday(LocalDate.parse("1998-03-25"))
                .build();
        when(userService.updateUser(user1)).thenReturn(user1);
        this.mockMvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testShouldReturnStatusIsOkThenRemoveUser() throws Exception {
        User user1 = User.builder()
                .id(1L)
                .name("Alex")
                .email("spring.a@yandex.ru")
                .login("alexSpring")
                .birthday(LocalDate.parse("1998-03-25"))
                .build();
        when(userService.removeUser(user1)).thenReturn(user1);
        this.mockMvc.perform(delete("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}