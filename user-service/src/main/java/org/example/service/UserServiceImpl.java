package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.UserEventDto;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.exception.ServiceUnavailableException;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_SERVICE_CB = "userService";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KafkaTemplate<String, UserEventDto> kafkaTemplate;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(USER_SERVICE_CB);
        return circuitBreaker.run(() -> {
            // Проверка существования email
            if (userRepository.existsByEmail(userRequestDto.getEmail())) {
                log.error("Email {} already exists", userRequestDto.getEmail());
                throw new IllegalArgumentException("Email already exists");
            }

            // Создание и сохранение пользователя
            User user = userMapper.toEntity(userRequestDto);
            User savedUser = userRepository.save(user);

            // Отправка события в Kafka
            sendUserEvent(savedUser, UserEventDto.EventType.CREATED);
            log.info("User created successfully with id: {}", savedUser.getId());

            return userMapper.toDto(savedUser);
        }, throwable -> {
            log.error("User service unavailable - createUser failed: {}", throwable.getMessage());
            throw new ServiceUnavailableException("User service is currently unavailable. Please try again later.");
        });
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(USER_SERVICE_CB);
        return circuitBreaker.run(() -> {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("User not found with id: {}", id);
                        return new UserNotFoundException(id);
                    });
            log.info("Retrieved user with id: {}", id);
            return userMapper.toDto(user);
        }, throwable -> {
            log.error("User service unavailable - getUserById failed: {}", throwable.getMessage());
            throw new ServiceUnavailableException("User service is currently unavailable. Please try again later.");
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(USER_SERVICE_CB);
        return circuitBreaker.run(() -> {
            List<UserResponseDto> users = userRepository.findAll().stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
            log.info("Retrieved {} users", users.size());
            return users;
        }, throwable -> {
            log.error("User service unavailable - getAllUsers failed: {}", throwable.getMessage());
            throw new ServiceUnavailableException("User service is currently unavailable. Please try again later.");
        });
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(USER_SERVICE_CB);
        return circuitBreaker.run(() -> {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("User not found with id: {}", id);
                        return new UserNotFoundException(id);
                    });

            // Проверка email на уникальность (исключая текущего пользователя)
            if (userRepository.existsByEmail(userRequestDto.getEmail(), id)) {
                log.error("Email {} already exists for another user", userRequestDto.getEmail());
                throw new IllegalArgumentException("Email already exists");
            }

            // Обновление данных пользователя
            existingUser.setName(userRequestDto.getName());
            existingUser.setEmail(userRequestDto.getEmail());
            existingUser.setAge(userRequestDto.getAge());

            User updatedUser = userRepository.save(existingUser);
            log.info("User updated successfully with id: {}", id);

            return userMapper.toDto(updatedUser);
        }, throwable -> {
            log.error("User service unavailable - updateUser failed: {}", throwable.getMessage());
            throw new ServiceUnavailableException("User service is currently unavailable. Please try again later.");
        });
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(USER_SERVICE_CB);
        circuitBreaker.run(() -> {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("User not found with id: {}", id);
                        return new UserNotFoundException(id);
                    });

            // Отправка события в Kafka перед удалением
            sendUserEvent(user, UserEventDto.EventType.DELETED);

            userRepository.delete(user);
            log.info("User deleted successfully with id: {}", id);
            return null;
        }, throwable -> {
            log.error("User service unavailable - deleteUser failed: {}", throwable.getMessage());
            throw new ServiceUnavailableException("User service is currently unavailable. Please try again later.");
        });
    }

    private void sendUserEvent(User user, UserEventDto.EventType eventType) {
        try {
            UserEventDto event = new UserEventDto();
            event.setEventType(eventType);
            event.setEmail(user.getEmail());
            event.setName(user.getName());

            kafkaTemplate.send("user-events", event);
            log.info("Sent {} event for user {} to Kafka", eventType, user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send {} event for user {} to Kafka: {}",
                    eventType, user.getEmail(), e.getMessage());
        }
    }
}