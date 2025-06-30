package org.example.service;

import org.example.dto.UserDTO;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private UserService userService;

    @Test
    void createUserShouldReturnUserDTOWhenValidInput() {
        // Arrange
        UserDTO inputDTO = new UserDTO(null, "Test", "test@example.com", 25, null);
        User savedUser = new User(1L, "Test", "test@example.com", 25, null);

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserDTO result = userService.createUser(inputDTO);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getAllUsersShouldReturnListOfUserDTOs() {
        // Arrange
        User user = new User(1L, "Test", "test@example.com", 25, null);
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        // Act
        List<UserDTO> result = userService.getAllUsers();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void updateUserShouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.updateUser(999L, new UserDTO()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }
}