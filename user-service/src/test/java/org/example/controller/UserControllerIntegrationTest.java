package org.example.controller;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.service.UserService;
import org.example.controller.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void shouldCreateUser() throws Exception {
        UserRequestDto userRequest = new UserRequestDto();
        userRequest.setName("John Doe");
        userRequest.setEmail("john@example.com");
        userRequest.setAge(30);

        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setId(1L);
        userResponse.setName("John Doe");
        userResponse.setEmail("john@example.com");
        userResponse.setAge(30);
        userResponse.setCreatedAt(LocalDateTime.now());

        given(userService.createUser(any(UserRequestDto.class))).willReturn(userResponse);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void shouldReturnBadRequestWhenEmailExists() throws Exception {
        UserRequestDto userRequest = new UserRequestDto();
        userRequest.setName("John Doe");
        userRequest.setEmail("john@example.com");
        userRequest.setAge(30);

        given(userService.createUser(any(UserRequestDto.class)))
                .willThrow(new IllegalArgumentException("Email already exists"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setId(1L);
        userResponse.setName("John Doe");
        userResponse.setEmail("john@example.com");
        userResponse.setAge(30);
        userResponse.setCreatedAt(LocalDateTime.now());

        given(userService.getAllUsers()).willReturn(Collections.singletonList(userResponse));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UserRequestDto userRequest = new UserRequestDto();
        userRequest.setName("John Updated");
        userRequest.setEmail("john.updated@example.com");
        userRequest.setAge(31);

        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setId(1L);
        userResponse.setName("John Updated");
        userResponse.setEmail("john.updated@example.com");
        userResponse.setAge(31);
        userResponse.setCreatedAt(LocalDateTime.now());

        given(userService.updateUser(anyLong(), any(UserRequestDto.class))).willReturn(userResponse);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@example.com"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}