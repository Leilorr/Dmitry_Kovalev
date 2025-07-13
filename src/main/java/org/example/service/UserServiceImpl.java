package org.example.service;


import lombok.RequiredArgsConstructor;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.example.dto.UserEventDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KafkaTemplate<String, UserEventDto> kafkaTemplate;

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = userMapper.toEntity(userRequestDto);
        User savedUser = userRepository.save(user);

        // Отправка события в Kafka
        UserEventDto event = new UserEventDto();
        event.setEventType("CREATED");
        event.setEmail(savedUser.getEmail());
        event.setName(savedUser.getName());
        kafkaTemplate.send("user-events", event);

        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Отправка события в Kafka перед удалением
        UserEventDto event = new UserEventDto();
        event.setEventType("DELETED");
        event.setEmail(user.getEmail());
        event.setName(user.getName());
        kafkaTemplate.send("user-events", event);

        userRepository.delete(user);
    }
}