package java.org.example.service;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldSaveUserSuccessfully() {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setName("John Doe");
        requestDto.setEmail("john@example.com");
        requestDto.setAge(30);

        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setAge(30);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("John Doe");
        savedUser.setEmail("john@example.com");
        savedUser.setAge(30);
        savedUser.setCreatedAt(LocalDateTime.now());

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(1L);
        responseDto.setName("John Doe");
        responseDto.setEmail("john@example.com");
        responseDto.setAge(30);
        responseDto.setCreatedAt(savedUser.getCreatedAt());

        given(userMapper.toEntity(requestDto)).willReturn(user);
        given(userRepository.save(user)).willReturn(savedUser);
        given(userMapper.toDto(savedUser)).willReturn(responseDto);
        given(userRepository.existsByEmail("john@example.com")).willReturn(false);

        UserResponseDto result = userService.createUser(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void shouldReturnAllUsers() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setAge(30);
        user.setCreatedAt(LocalDateTime.now());

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(1L);
        responseDto.setName("John Doe");
        responseDto.setEmail("john@example.com");
        responseDto.setAge(30);
        responseDto.setCreatedAt(user.getCreatedAt());

        given(userRepository.findAll()).willReturn(Collections.singletonList(user));
        given(userMapper.toDto(user)).willReturn(responseDto);

        List<UserResponseDto> users = userService.getAllUsers();

        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
        assertEquals("John Doe", users.get(0).getName());
    }

    @Test
    void shouldReturnUserById() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setAge(30);
        user.setCreatedAt(LocalDateTime.now());

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(userId);
        responseDto.setName("John Doe");
        responseDto.setEmail("john@example.com");
        responseDto.setAge(30);
        responseDto.setCreatedAt(user.getCreatedAt());

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userMapper.toDto(user)).willReturn(responseDto);

        UserResponseDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("John Doe", result.getName());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void shouldUpdateUser() {
        Long userId = 1L;
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setName("John Updated");
        requestDto.setEmail("john.updated@example.com");
        requestDto.setAge(31);

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John Doe");
        existingUser.setEmail("john@example.com");
        existingUser.setAge(30);
        existingUser.setCreatedAt(LocalDateTime.now());

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john.updated@example.com");
        updatedUser.setAge(31);
        updatedUser.setCreatedAt(existingUser.getCreatedAt());

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(userId);
        responseDto.setName("John Updated");
        responseDto.setEmail("john.updated@example.com");
        responseDto.setAge(31);
        responseDto.setCreatedAt(updatedUser.getCreatedAt());

        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
        given(userRepository.save(any(User.class))).willReturn(updatedUser);
        given(userMapper.toDto(updatedUser)).willReturn(responseDto);

        UserResponseDto result = userService.updateUser(userId, requestDto);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("John Updated", result.getName());
        assertEquals("john.updated@example.com", result.getEmail());
    }

    @Test
    void shouldDeleteUser() {
        Long userId = 1L;
        given(userRepository.existsById(userId)).willReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}